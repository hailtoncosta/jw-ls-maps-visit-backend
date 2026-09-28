package com.visit.jw_ls_maps_visit.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.visit.jw_ls_maps_visit.security.CurrentUser;

@RestController
@RequestMapping("/api/system-backup")
public class SystemBackupController {
    private final DataSource dataSource;
    private final CurrentUser currentUser;
    private final Path uploads;

    public SystemBackupController(DataSource dataSource, CurrentUser currentUser,
            @Value("${app.upload-dir:uploads}") String uploadDir) {
        this.dataSource = dataSource;
        this.currentUser = currentUser;
        this.uploads = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/export")
    public ResponseEntity<StreamingResponseBody> export() {
        // Check before response streaming starts; the stream runs on a different thread.
        if (!currentUser.isAdmin()) throw new org.springframework.security.access.AccessDeniedException("Somente o Administrador Geral pode baixar o backup completo.");
        String filename = "LSMaps_Backup_Completo_" + LocalDate.now() + ".zip";
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
            .header(HttpHeaders.CACHE_CONTROL, "no-store")
            .contentType(MediaType.parseMediaType("application/zip"))
            .body(this::writeBackup);
    }

    private void writeBackup(OutputStream output) throws IOException {
        try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8);
             Connection connection = dataSource.getConnection()) {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);
            List<String> tables = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet rs = statement.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE' ORDER BY table_name")) {
                while (rs.next()) tables.add(rs.getString(1));
            }
            for (String table : tables) writeTable(zip, connection, table);
            connection.rollback();
            zip.putNextEntry(new ZipEntry("LEIA-ME.txt"));
            write(zip, "Backup completo LS Maps Visit - " + Instant.now() + "\n" +
                "Tabelas do esquema public: " + tables.size() + ". Cada tabela está em dados/<nome>.json com colunas e registros.\n" +
                "Arquivos enviados ao sistema estão em uploads/.\n" +
                "Este arquivo contém dados pessoais, hashes de senhas e tokens; guarde-o em local seguro.\n" +
                "Este exportador não faz restauração automática. O importador de endereços existente não aceita este ZIP.\n");
            zip.closeEntry();
            if (Files.isDirectory(uploads)) {
                try (var paths = Files.walk(uploads)) {
                    for (Path path : paths.filter(Files::isRegularFile).filter(p -> !Files.isSymbolicLink(p)).toList()) {
                        String entry = "uploads/" + uploads.relativize(path).toString().replace('\\', '/');
                        zip.putNextEntry(new ZipEntry(entry));
                        Files.copy(path, zip);
                        zip.closeEntry();
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            throw new IOException("Falha ao exportar os dados do banco", e);
        }
    }

    private void writeTable(ZipOutputStream zip, Connection connection, String table) throws java.sql.SQLException, IOException {
        // Table names come from PostgreSQL's information_schema; quote embedded quotes as well.
        String quotedTable = "\"" + table.replace("\"", "\"\"") + "\"";
        zip.putNextEntry(new ZipEntry("dados/" + table.replace('/', '_') + ".json"));
        try (Statement statement = connection.createStatement()) {
            statement.setFetchSize(500);
            try (ResultSet rows = statement.executeQuery("SELECT * FROM public." + quotedTable)) {
                ResultSetMetaData meta = rows.getMetaData();
                int columns = meta.getColumnCount();
                write(zip, "{\"table\":" + json(table) + ",\"columns\":[");
                for (int i = 1; i <= columns; i++) {
                    if (i > 1) write(zip, ",");
                    write(zip, "{\"name\":" + json(meta.getColumnName(i)) + ",\"type\":" + json(meta.getColumnTypeName(i)) + "}");
                }
                write(zip, "],\"rows\":[");
                boolean first = true;
                while (rows.next()) {
                    if (!first) write(zip, ",");
                    first = false;
                    StringBuilder record = new StringBuilder("[");
                    for (int i = 1; i <= columns; i++) {
                        if (i > 1) record.append(',');
                        Object value = rows.getObject(i);
                        record.append(value == null ? "null" : json(value instanceof byte[] bytes ? "\\x" + HexFormat.of().formatHex(bytes) : rows.getString(i)));
                    }
                    write(zip, record.append(']').toString());
                }
                write(zip, "]}");
            }
        } finally {
            zip.closeEntry();
        }
    }

    private static void write(ZipOutputStream zip, String value) throws IOException {
        zip.write(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String json(String value) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"' -> out.append("\\\"");
                case '\\' -> out.append("\\\\");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                default -> { if (ch < 0x20) out.append(String.format("\\u%04x", (int) ch)); else out.append(ch); }
            }
        }
        return out.append('"').toString();
    }
}
