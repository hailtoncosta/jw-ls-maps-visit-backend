package com.visit.jw_ls_maps_visit.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table(name = "enderecos")
public class Endereco extends BaseEntity {
    
    @JsonProperty("congregation")
    @Column (name = "congregacao")
    private String congregacao;

    @JsonProperty("congregation_id")
    @Column (name = "congregacao_id")
    private UUID congregacaoId;

    @JsonProperty("circuito_id")
    @Column (name = "circuito_id")
    private UUID circuitoId;

    @JsonProperty("card_number")
    @Column (name = "card_numero")
    private Integer cardNumero;

    @JsonProperty("name")
    @Column (name = "nome")
    private String nome;

    @JsonProperty("gender")
    @Column (name = "genero")
    private String genero;

    @JsonProperty("age")
    @Column (name = "idade")
    private Integer idade;

    @JsonProperty("street")
    @Column (name = "rua")
    private String rua;
    
    @JsonProperty("neighborhood")
    @Column (name = "bairro")
    private String bairro;
    
    @JsonProperty("city")
    @Column (name = "cidade")
    private String cidade;

    @JsonProperty("state")
    @Column (name = "estado")
    private String estado;

    @JsonProperty("lat")
    @Column (name = "latitude")
    private Double latitude;

    @JsonProperty("lng")
    @Column (name = "longitude")
    private Double longitude;

    @JsonProperty("maps_link")
    @Column (name = "maps_link", length = 1000)
    private String mapsLink;

    @JsonProperty("phone")
    @Column (name = "telefone")
    private String telefone;

    @JsonProperty("observations")
    @Column (name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @JsonProperty("last_visit_date")
    @Column (name = "data_ultima_visita")
    private String dataUltimaVisita;

    @JsonProperty("last_visit_time")
    @Column (name = "hora_ultima_visita")
    private String horaUltimaVisita;

    @JsonProperty("last_visited_by")
    @Column (name = "visitado_por")
    private String visitadoPor;

    @JsonProperty("visit_count")
    @Column (name = "quantidade_visitas")
    private Integer QuantidadeVisitas;

    @Column (name = "status")
    private String status;

    @Column (name = "situacao")
    private String situacao;

    @JsonProperty("reference_point")
    @Column (name = "ponto_referencia")
    private String pontoReferencia;

    @JsonProperty("house_photo")
    @Column (name = "foto_casa")
    private String fotoCasa;
    
    @JsonProperty("best_time")
    @Column (name = "foto_morador")
    private String melhorHorario;

    @JsonProperty("language")
    @Column (name = "idioma")
    private String idioma;

    @JsonProperty("language_level")
    @Column (name = "nivel_idioma")
    private String nivelIdioma;

    @JsonProperty("sign_language_level")
    @Column (name = "nivel_idioma_sinal")
    private String nivelIdiomaSinal;

    @JsonProperty("deleted")
    @Column (name = "excluido")
    private Boolean excluido;

    @JsonProperty("imported")
    @Column (name = "importado")
    private Boolean importado;

    @JsonProperty("import_source")
    @Column (name = "fonte_importacao")
    private String fonteImportacao;

    @Column (name = "merged")
    private Boolean merged;

    @JsonProperty("visitor_origin")
    @Column (name = "origem_visitante")
    private Boolean origemVisitante;

    @JsonProperty("source_address_id")
    @Column (name = "endereco_origem_id")
    private UUID enderecoOrigemId;

    @JsonProperty("import_date")
    @Column (name = "data_importacao")
    private OffsetDateTime dataImportacao;

    @JsonProperty("import_user")
    @Column (name = "usuario_importacao")
    private String usuarioImportacao;

    @JsonProperty("import_key")
    @Column (name = "chave_importacao")
    private String chaveImportacao;

    @JsonProperty("authorized_users")
    @ElementCollection
    @CollectionTable (name = "endereco_autorizados_usuarios", joinColumns = @JoinColumn(name = "address_id"))
    @Column (name = "usuario_id")
    private Set<String> authorizedUsers = new HashSet<>();
    
}
