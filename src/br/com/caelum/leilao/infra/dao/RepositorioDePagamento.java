package br.com.caelum.leilao.infra.dao;

import br.com.caelum.leilao.dominio.Pagamento;

public interface RepositorioDePagamento {
	
	void salva(Pagamento pagamento);
}
