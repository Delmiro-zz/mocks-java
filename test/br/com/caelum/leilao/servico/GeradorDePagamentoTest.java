package br.com.caelum.leilao.servico;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import java.util.Arrays;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamento;


public class GeradorDePagamentoTest {
	
	@Test
	public void devemosGerarPagamentoParaUmLeilaoEncerrado() {
		LeilaoDao leilaoDao = mock(LeilaoDao.class);
		RepositorioDePagamento pagamentos = mock(RepositorioDePagamento.class);
		Avaliador avaliador = mock(Avaliador.class);
		
		Leilao leilao = new CriadorDeLeilao().para("Playstation")
				.lance(new Usuario("Diego"), 2000.0)
				.lance(new Usuario("Maria"), 3000.0).constroi();
		
		when(leilaoDao.encerrados()).thenReturn(Arrays.asList(leilao));
		when(avaliador.getMaiorLance()).thenReturn(3000.0);
		
		GeradorDePagamento gerador = new GeradorDePagamento(leilaoDao, pagamentos, avaliador);
		gerador.gera();
		
		ArgumentCaptor<Pagamento> argumentoCapturado = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumentoCapturado.capture());
		
		assertEquals(3000.0, argumentoCapturado.getValue().getValor(), 0.00001);
	}
}
