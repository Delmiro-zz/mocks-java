package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.junit.Test;
import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.email.Carteiro;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV Samsung 65").naData(dataAntiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(dataAntiga).constroi();
		
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		Carteiro carteiro = mock(Carteiro.class);
		LeilaoDao cloneDao = mock(LeilaoDao.class);
		
		when(cloneDao.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(cloneDao, carteiro);
		encerrador.encerra();
		
		assertEquals(2 , encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
	}
	
	@Test
	public void deveAtualizarLeiloesEncerrados() {
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV Samsung 65").naData(dataAntiga).constroi();
		
		Carteiro carteiro = mock(Carteiro.class);
		LeilaoDao dao = mock(LeilaoDao.class);
		
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1));
	
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao, carteiro);
		encerrador.encerra();
		
		verify(dao).atualiza(leilao1);
	}
	
	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
		Calendar dataOntem = Calendar.getInstance();
		dataOntem.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(dataOntem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(dataOntem).constroi();
		
		Carteiro carteiro = mock(Carteiro.class);
		LeilaoDao dao = mock(LeilaoDao.class);
		
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		
		EncerradorDeLeilao encerradorDeLeilao =  new EncerradorDeLeilao(dao, carteiro);
		encerradorDeLeilao.encerra();
		
		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
		
		verify(dao, never()).atualiza(leilao1);
		verify(dao, never()).atualiza(leilao2);
	}
	
	@Test
	public void deveContinuarAExecucaoMesmoQuandoDaoFalhar() {
		Calendar ontem = Calendar.getInstance();
		ontem.set(1999, 1 , 10);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(ontem).constroi();
		
		LeilaoDao leilaoDao = mock(LeilaoDao.class);
		Carteiro carteiro = mock(Carteiro.class);
		
		when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		doThrow(new RuntimeException()).when(leilaoDao).atualiza(leilao1);
		
		EncerradorDeLeilao encerradorDeLeilao = new EncerradorDeLeilao(leilaoDao, carteiro);
		encerradorDeLeilao.encerra();
		
		//garantido que o metodo envia nao sera invocado
		verify(carteiro, times(0)).envia(leilao1);
		
		verify(leilaoDao).atualiza(leilao2);
		verify(carteiro).envia(leilao2);
	}
	
}
