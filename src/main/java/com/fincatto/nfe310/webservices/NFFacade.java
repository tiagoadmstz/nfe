package com.fincatto.nfe310.webservices;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

import com.fincatto.dfe.classes.DFModelo;
import com.fincatto.dfe.classes.DFUnidadeFederativa;
import com.fincatto.dfe.webservices.DFSocketFactory;
import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.classes.cadastro.NFRetornoConsultaCadastro;
import com.fincatto.nfe310.classes.evento.NFEnviaEventoRetorno;
import com.fincatto.nfe310.classes.evento.downloadnf.NFDownloadNFeRetorno;
import com.fincatto.nfe310.classes.evento.inutilizacao.NFRetornoEventoInutilizacao;
import com.fincatto.nfe310.classes.evento.manifestacaodestinatario.NFTipoEventoManifestacaoDestinatario;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetornoDados;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.consulta.NFNotaConsultaRetorno;
import com.fincatto.nfe310.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;

import br.inf.portalfiscal.nfe.RetDistDFeInt;
import br.inf.portalfiscal.nfe.TRetEnviNFe;

public class NFFacade {

    private final WSLoteEnvio wsLoteEnvio;
    private final WSLoteConsulta wsLoteConsulta;
    private final WSStatusConsulta wsStatusConsulta;
    private final WSNotaConsulta wsNotaConsulta;
    private final WSCartaCorrecao wsCartaCorrecao;
    private final WSCancelamento wsCancelamento;
    private final WSConsultaCadastro wsConsultaCadastro;
    private final WSInutilizacao wsInutilizacao;
    private final WSManifestacaoDestinatario wSManifestacaoDestinatario;
    private final WSNotaDownload wsNotaDownload;
    private final WSDistribuicaoDocumentoFiscal wsDistribuicaoDocumentoFiscal;

    public NFFacade(final NFeConfig config) throws IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
        HttpsURLConnection.setDefaultSSLSocketFactory(new DFSocketFactory(config).createSSLContext().getSocketFactory());

        // inicia os servicos disponíveis
        this.wsLoteEnvio = new WSLoteEnvio(config);
        this.wsLoteConsulta = new WSLoteConsulta(config);
        this.wsStatusConsulta = new WSStatusConsulta(config);
        this.wsNotaConsulta = new WSNotaConsulta(config);
        this.wsCartaCorrecao = new WSCartaCorrecao(config);
        this.wsCancelamento = new WSCancelamento(config);
        this.wsConsultaCadastro = new WSConsultaCadastro(config);
        this.wsInutilizacao = new WSInutilizacao(config);
        this.wSManifestacaoDestinatario = new WSManifestacaoDestinatario(config);
        this.wsNotaDownload = new WSNotaDownload(config);
        this.wsDistribuicaoDocumentoFiscal = new WSDistribuicaoDocumentoFiscal(config);

    }

    /**
     * Faz o envio de lote para a Sefaz
     *
     * @param lote o lote a ser enviado para a Sefaz
     * @return dados do lote retornado pelo webservice, alem do lote assinado
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteEnvioRetornoDados enviaLote(final NFLoteEnvio lote) throws Exception {
        if (lote.getIndicadorProcessamento().equals(NFLoteIndicadorProcessamento.PROCESSAMENTO_SINCRONO)) {
            throw new IllegalStateException("Utilize o método enviaLoteSincrono");
        }
        return this.wsLoteEnvio.enviaLote(lote);
    }

    /**
     * Faz o envio de lote para a Sefaz
     *
     * @param lote o lote a ser enviado para a Sefaz
     * @return dados do lote retornado pelo webservice, alem do lote assinado
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public TRetEnviNFe enviaLoteSincrono(final NFLoteEnvio lote) throws Exception {
        if (lote.getIndicadorProcessamento().equals(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO)) {
            throw new IllegalStateException("Utilize o método enviaLote");
        }
        return this.wsLoteEnvio.enviaLoteSincrono(lote);
    }

    /**
     * Faz o envio assinado para a Sefaz de NF-e e NFC-e
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param loteAssinadoXml lote assinado no formato XML
     * @param modelo          modelo da nota (NF-e ou NFC-e)
     * @return dados do lote retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteEnvioRetorno enviaLoteAssinado(final String loteAssinadoXml, final DFModelo modelo) throws Exception {
        return this.wsLoteEnvio.enviaLoteAssinado(loteAssinadoXml, modelo);
    }

    /**
     * Faz o envio assinado para a Sefaz de NF-e e NFC-e
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param loteAssinadoXml lote assinado no formato XML
     * @param modelo          modelo da nota (NF-e ou NFC-e)
     * @return dados do lote retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public TRetEnviNFe enviaLoteAssinadoSincrono(final String loteAssinadoXml, final DFModelo modelo) throws Exception {
        return this.wsLoteEnvio.enviaLoteAssinadoSincrono(loteAssinadoXml, modelo);
    }

    /**
     * Faz a consulta do lote na Sefaz (NF-e e NFC-e)
     *
     * @param numeroRecibo numero do recibo do processamento
     * @param modelo       modelo da nota (NF-e ou NFC-e)
     * @return dados de consulta de lote retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFLoteConsultaRetorno consultaLote(final String numeroRecibo, final DFModelo modelo) throws Exception {
        return this.wsLoteConsulta.consultaLote(numeroRecibo, modelo);
    }

    /**
     * Faz a consulta de status responsavel pela UF
     *
     * @param uf     uf UF que deseja consultar o status do sefaz responsavel
     * @param modelo modelo da nota (NF-e ou NFC-e)
     * @return dados da consulta de status retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFStatusServicoConsultaRetorno consultaStatus(final DFUnidadeFederativa uf, final DFModelo modelo) throws Exception {
        return this.wsStatusConsulta.consultaStatus(uf, modelo);
    }

    /**
     * Faz a consulta da nota
     *
     * @param chaveDeAcesso chave de acesso da nota
     * @return dados da consulta da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFNotaConsultaRetorno consultaNota(final String chaveDeAcesso) throws Exception {
        return this.wsNotaConsulta.consultaNota(chaveDeAcesso);
    }

    /**
     * Faz a correcao da nota
     *
     * @param chaveDeAcesso          chave de acesso da nota
     * @param textoCorrecao          texto de correcao
     * @param numeroSequencialEvento numero sequencial de evento, esse numero nao pode ser repetido!
     * @return dados da correcao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno corrigeNota(final String chaveDeAcesso, final String textoCorrecao, final int numeroSequencialEvento) throws Exception {
        return this.wsCartaCorrecao.corrigeNota(chaveDeAcesso, textoCorrecao, numeroSequencialEvento);
    }

    /**
     * Faz a correcao da nota com o evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados da correcao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno corrigeNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wsCartaCorrecao.corrigeNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Faz o cancelamento da nota
     *
     * @param chave     chave de acesso da nota
     * @param numeroProtocolo numero do protocolo da nota
     * @param motivo          motivo do cancelamento
     * @return dados do cancelamento da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno cancelaNota(final String chave, final String numeroProtocolo, final String motivo) throws Exception {
        return this.wsCancelamento.cancelaNota(chave, numeroProtocolo, motivo);
    }

    /**
     * Faz o cancelamento da nota com evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados do cancelamento da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno cancelaNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wsCancelamento.cancelaNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Inutiliza a nota com o evento assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param eventoAssinadoXml evento assinado em XML
     * @param modelo            modelo da nota (NF-e ou NFC-e)
     * @return dados da inutilizacao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoEventoInutilizacao inutilizaNotaAssinada(final String eventoAssinadoXml, final DFModelo modelo) throws Exception {
        return this.wsInutilizacao.inutilizaNotaAssinada(eventoAssinadoXml, modelo);
    }

    /**
     * Inutiliza a nota
     *
     * @param anoInutilizacaoNumeracao ano de inutilizacao
     * @param cnpjEmitente             CNPJ emitente da nota
     * @param serie                    serie da nota
     * @param numeroInicial            numero inicial da nota
     * @param numeroFinal              numero final da nota
     * @param justificativa            justificativa da inutilizacao
     * @param modelo                   modelo da nota (NF-e ou NFC-e)
     * @return dados da inutilizacao da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoEventoInutilizacao inutilizaNota(final int anoInutilizacaoNumeracao, final String cnpjEmitente, final String serie, final String numeroInicial, final String numeroFinal, final String justificativa, final DFModelo modelo) throws Exception {
        return this.wsInutilizacao.inutilizaNota(anoInutilizacaoNumeracao, cnpjEmitente, serie, numeroInicial, numeroFinal, justificativa, modelo);
    }

    /**
     * Realiza a consulta de cadastro de pessoa juridica com inscricao estadual
     *
     * @param cnpj CNPJ da pessoa juridica
     * @param uf   UF da pessoa juridica
     * @return dados da consulta da pessoa juridica retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFRetornoConsultaCadastro consultaCadastro(final String cnpj, final DFUnidadeFederativa uf) throws Exception {
        return this.wsConsultaCadastro.consultaCadastro(cnpj, uf);
    }

    /**
     * Faz a manifestação do destinatário da nota
     *
     * @param chave chave de acesso da nota
     * @param tipoEvento  tipo do evento da manifestacao do destinatario
     * @param motivo      motivo do cancelamento
     * @param cnpj        cnpj do autor do evento
     * @return dados da manifestacao do destinatario da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno manifestaDestinatarioNota(final String chave, final NFTipoEventoManifestacaoDestinatario tipoEvento, final String motivo, final String cnpj) throws Exception {
        return this.wSManifestacaoDestinatario.manifestaDestinatarioNota(chave, tipoEvento, motivo, cnpj);
    }

    /**
     * Faz a manifestação do destinatário da nota com evento ja assinado
     * ATENCAO: Esse metodo deve ser utilizado para assinaturas A3
     *
     * @param chave       chave de acesso da nota
     * @param eventoAssinadoXml evento ja assinado em formato XML
     * @return dados da manifestacao do destinatario da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFEnviaEventoRetorno manifestaDestinatarioNotaAssinada(final String chave, final String eventoAssinadoXml) throws Exception {
        return this.wSManifestacaoDestinatario.manifestaDestinatarioNotaAssinada(chave, eventoAssinadoXml);
    }

    /**
     * Faz o download do xml da nota para um cnpj
     * Informando até 10 chaves de acesso
     *
     * @param cnpj  para quem foi emitida a nota
     * @param chave chave de acesso da nota
     * @return dados do download da nota retornado pelo webservice
     * @throws Exception caso nao consiga gerar o xml ou problema de conexao com o sefaz
     */
    public NFDownloadNFeRetorno downloadNota(final String cnpj, final String chave) throws Exception {
        return this.wsNotaDownload.downloadNota(cnpj, chave);
    }

    /**
     * a) distNSU – Distribuição de Conjunto de DF-e a Partir do NSU Informado
     *
     * Disponibiliza para os atores da NF-e informações e documentos fiscais eletrônicos de seu interesse.
     * A distribuição é realizada para emitentes, destinatários, transportadores e terceiros informados no
     * conteúdo da NF-e respectivamente no grupo do Emitente (tag:emit, id:C01), no grupo do Destinatário
     * (tag:dest, id:E01), no grupo do Transportador (tag:transporta, id:X03) e no grupo de pessoas físicas
     * autorizadas a acessar o XML (tag:autXML, id:GA01)
     * Referência: NT2014.002_v1.02_WsNFeDistribuicaoDFe
     *
     * @param cnpj
     * @param ultNSU último número sequencial único
     * @param unidadeFederativaAutorizador
     * @return
     * @throws Exception
     */
    public RetDistDFeInt pedidoDistribuicao(final String cnpj, final String ultNSU, final DFUnidadeFederativa unidadeFederativaAutorizador) throws Exception {
        return this.wsDistribuicaoDocumentoFiscal.pedidoDistribuicao(cnpj, ultNSU, unidadeFederativaAutorizador);
    }

    /**
     * b) consNSU – Consulta DF-e Vinculado ao NSU Informado
     *
     * Disponibiliza para os atores da NF-e informações e documentos fiscais eletrônicos de seu interesse.
     * A distribuição é realizada para emitentes, destinatários, transportadores e terceiros informados no
     * conteúdo da NF-e respectivamente no grupo do Emitente (tag:emit, id:C01), no grupo do Destinatário
     * (tag:dest, id:E01), no grupo do Transportador (tag:transporta, id:X03) e no grupo de pessoas físicas
     * autorizadas a acessar o XML (tag:autXML, id:GA01)
     * Referência: NT2014.002_v1.02_WsNFeDistribuicaoDFe
     *
     * @param cnpj
     * @param nsu
     * @param unidadeFederativaAutorizador
     * @return
     * @throws Exception
     */
    public RetDistDFeInt pedidoDistribuicaoNSU(final String cnpj, final String nsu, final DFUnidadeFederativa unidadeFederativaAutorizador) throws Exception {
        return this.wsDistribuicaoDocumentoFiscal.pedidoDistribuicaoNSU(cnpj, nsu, unidadeFederativaAutorizador);
    }

    /**
     * c) consChNFe – Consulta de NF-e por Chave de Acesso Informada
     *
     * Disponibiliza para os atores da NF-e informações e documentos fiscais eletrônicos de seu interesse.
     * A distribuição é realizada para emitentes, destinatários, transportadores e terceiros informados no
     * conteúdo da NF-e respectivamente no grupo do Emitente (tag:emit, id:C01), no grupo do Destinatário
     * (tag:dest, id:E01), no grupo do Transportador (tag:transporta, id:X03) e no grupo de pessoas físicas
     * autorizadas a acessar o XML (tag:autXML, id:GA01)
     * Referência: NT2014.002_v1.02_WsNFeDistribuicaoDFe
     *
     *
     * @param cnpj
     * @param chave
     * @param unidadeFederativaAutorizador
     * @return
     * @throws Exception
     */
    public RetDistDFeInt pedidoDistribuicaoChave(final String cnpj, final String chave, final DFUnidadeFederativa unidadeFederativaAutorizador) throws Exception {
        return this.wsDistribuicaoDocumentoFiscal.pedidoDistribuicaoChave(cnpj, chave, unidadeFederativaAutorizador);
    }

}