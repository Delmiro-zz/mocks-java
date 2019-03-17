package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;

public class GeradorDePagamento {

	private LeilaoDao leilaoDao;
	private Avaliador avaliador;
	private RepositorioDePagamento pagamentos;

	public GeradorDePagamento(LeilaoDao leilaoDao, RepositorioDePagamento pagamentos, Avaliador avaliador) {
		this.leilaoDao = leilaoDao;
		this.pagamentos = pagamentos;
		this.avaliador = avaliador;
	}

	public void gera() {
		List<Leilao> leiloesEncerrados = this.leilaoDao.encerrados();
		
		for (Leilao leilao : leiloesEncerrados) {
			this.avaliador.avalia(leilao);
		}
		Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), Calendar.getInstance());
		this.pagamentos.salva(novoPagamento);
	}
	
	
}
