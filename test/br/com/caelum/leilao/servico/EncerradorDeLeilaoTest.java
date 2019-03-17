package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.junit.Test;
import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;

public class EncerradorDeLeilaoTest {
	
	@Test
	public void deveEncerrarLeiloesQueComecaramUmaSemanaAntes() {
		Calendar dataAntiga = Calendar.getInstance();
		dataAntiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV Samsung 65").naData(dataAntiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(dataAntiga).constroi();
		
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		LeilaoDao cloneDao = mock(LeilaoDao.class);
		
		when(cloneDao.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(cloneDao);
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
		
		LeilaoDao dao = mock(LeilaoDao.class);
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(dao);
		encerrador.encerra();
		
		verify(dao).atualiza(leilao1);
	}
	
	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
		Calendar dataOntem = Calendar.getInstance();
		dataOntem.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(dataOntem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(dataOntem).constroi();
		
		LeilaoDao dao = mock(LeilaoDao.class);
		
		when(dao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		
		EncerradorDeLeilao encerradorDeLeilao =  new EncerradorDeLeilao(dao);
		encerradorDeLeilao.encerra();
		
		assertEquals(0, encerradorDeLeilao.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
		
		verify(dao, never()).atualiza(leilao1);
		verify(dao, never()).atualiza(leilao2);
	}
}
