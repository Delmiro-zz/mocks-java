package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;
import br.com.caelum.leilao.infra.relogio.Relogio;
import br.com.caelum.leilao.infra.relogio.RelogioDoSistema;

public class GeradorDePagamento {

	private LeilaoDao leilaoDao;
	private Avaliador avaliador;
	private RepositorioDePagamento pagamentos;
	private Relogio relogio;

	public GeradorDePagamento(LeilaoDao leilaoDao, RepositorioDePagamento pagamentos, Avaliador avaliador, Relogio relogio) {
		this.leilaoDao = leilaoDao;
		this.pagamentos = pagamentos;
		this.avaliador = avaliador;
		this.relogio = relogio;
	}
	
	public GeradorDePagamento(LeilaoDao leilaoDao, RepositorioDePagamento pagamentos, Avaliador avaliador) {
		this(leilaoDao, pagamentos, avaliador, new RelogioDoSistema());
	}

	public void gera() {
		List<Leilao> leiloesEncerrados = this.leilaoDao.encerrados();
		
		for (Leilao leilao : leiloesEncerrados) {
			this.avaliador.avalia(leilao);
		}
		Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
		this.pagamentos.salva(novoPagamento);
	}

	private Calendar primeiroDiaUtil() {
		Calendar data = relogio.hoje();
		int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);
		
		if (diaDaSemana == Calendar.SATURDAY) {
			data.add(Calendar.DAY_OF_MONTH, 2);
		} else if (diaDaSemana == Calendar.SUNDAY) {
			data.add(Calendar.DAY_OF_MONTH, 1);
		}
		return data;
	}

}
