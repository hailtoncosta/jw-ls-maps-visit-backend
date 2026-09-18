package com.visit.jw_ls_maps_visit.model;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
    
    @Column (name = "congregacao")
    private String congregacao;

    @Column (name = "congregacao_id")
    private UUID congregacaoId;

    @Column (name = "circuito_id")
    private UUID circuitoId;

    @Column (name = "card_numero")
    private Integer cardNumero;

    @Column (name = "nome")
    private String nome;

    @Column (name = "genero")
    private String genero;

    @Column (name = "idade")
    private Integer idade;

    @Column (name = "rua")
    private String rua;
    
    @Column (name = "bairro")
    private String bairro;
    
    @Column (name = "cidade")
    private String cidade;

    @Column (name = "estado")
    private String estado;

    @Column (name = "latitude")
    private Double latitude;

    @Column (name = "longitude")
    private Double longitude;

    @Column (name = "telefone")
    private String telefone;

    @Column (name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @Column (name = "data_ultima_visita")
    private String dataUltimaVisita;

    @Column (name = "hora_ultima_visita")
    private String horaUltimaVisita;

    @Column (name = "visitado_por")
    private String visitadoPor;

    @Column (name = "quantidade_visitas")
    private Integer QuantidadeVisitas;

    @Column (name = "status")
    private String status;

    @Column (name = "situacao")
    private String situacao;

    @Column (name = "ponto_referencia")
    private String pontoReferencia;

    @Column (name = "foto_casa")
    private String fotoCasa;
    
    @Column (name = "foto_morador")
    private String melhorHorario;

    @Column (name = "idioma")
    private String idioma;

    @Column (name = "nivel_idioma")
    private String nivelIdioma;

    @Column (name = "nivel_idioma_sinal")
    private String nivelIdiomaSinal;

    @Column (name = "excluido")
    private Boolean excluido;

    @Column (name = "importado")
    private Boolean importado;

    @Column (name = "fonte_importacao")
    private String fonteImportacao;

    @Column (name = "merged")
    private Boolean merged;

    @Column (name = "origem_visitante")
    private Boolean origemVisitante;

    @Column (name = "endereco_origem_id")
    private UUID enderecoOrigemId;

    @Column (name = "data_importacao")
    private OffsetDateTime dataImportacao;

    @Column (name = "usuario_importacao")
    private String usuarioImportacao;

    @Column (name = "chave_importacao")
    private String chaveImportacao;

    @ElementCollection
    @CollectionTable (name = "endereco_autorizados_usuarios", joinColumns = @JoinColumn(name = "address_id"))
    @Column (name = "usuario_id")
    private Set<String> authorizedUsers = new HashSet<>();
    
}
