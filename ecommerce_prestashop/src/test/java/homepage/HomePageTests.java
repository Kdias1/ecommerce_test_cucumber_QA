package homepage;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		CarregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		// System.out.println(produtosNoCarrinho);
		assertThat(produtosNoCarrinho, is(0));
	}

	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

		System.out.println(nomeProduto_ProdutoPage);
		System.out.println(precoProduto_ProdutoPage);

		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}

	LoginPage loginPage;

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		// clicar no bot�o sign in na home page
		loginPage = homePage.clicarBotaoSignIn();

		// preencher usu�rio e senha
		loginPage.preencherEmail("carlakdias@gmail.com");
		loginPage.preencherPassword("Javatest1234*");

		// clicar no bot�o sign in para logar"
		loginPage.clicarBotaoSignIn();

		// validar se o usu�rio ta logado de fato

		assertThat(homePage.estaLogado("Carla Dias"), is(true));
		CarregarPaginaInicial();
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_Loginok.csv", numLinesToSkip = 1, delimiter = ';')
	public void testeLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario,
			String resultado) {

		// clicar no bot�o sign in na home page
		loginPage = homePage.clicarBotaoSignIn();

		// preencher usu�rio e senha
		loginPage.preencherEmail(email);
		loginPage.preencherPassword(password);

		// clicar no bot�o sign in para logar"
		loginPage.clicarBotaoSignIn();
		
		boolean esperado_LoginOk;
		if (resultado.equals("positivo"))
			esperado_LoginOk = true;
		else 
			esperado_LoginOk = false;

		// validar se o usu�rio ta logado de fato

		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_LoginOk));
		
		capturarTela(nomeTeste, resultado);
		
		if(esperado_LoginOk)
			homePage.clicarBotaoSignOut();
		
		CarregarPaginaInicial();
		
	}

	ModalProdutoPage modalProdutoPage;

	@Test
	public void incluirProdutoNoCarrinho_ProdutoConcuidoComSucesso() {

		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 2;

		// --pr�-condu��o
		// usu�rio logado
		if (!homePage.estaLogado("Carla Dias")) {
			testLoginComSucesso_UsuarioLogado();
		}
		// -- Teste
		// Selecionando Produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar cor
		produtoPage.selecionarCorPreta();

		// selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// adicionar carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();

		// Valida��es

		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));

		System.out.println(modalProdutoPage.obterDescricaoProduto());

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));

		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", " ");
		Double precoProduto = Double.parseDouble(modalProdutoPage.obterPrecoProduto().replace("$", " "));//alterei

		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", " ");
		Double subtotal = Double.parseDouble(subtotalString);

		Double subtotalCalculado = quantidadeProduto * precoProduto;
		assertThat(subtotal, is(subtotalCalculado));

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

	}

	// valores esperados

	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	int esperado_input_quantidadeProduto = 2;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;

	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subtotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalProduto + esperado_shippingTotal;
	Double esperado_totalTaxIncTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;
	String esperado_corProduto = "Black";
	String esperado_nomeCliente = "Carla Dias";

	CarrinhoPage carrinhoPage;

	@Test
	public void irParaCarrinho_InformacoesPersistidas() {
		// -- pr� condic�es
		// Produto Inclu�do na tela ModalProdutoPage
		incluirProdutoNoCarrinho_ProdutoConcuidoComSucesso();

		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();

		// Teste

		// Validar Todos os Elementos da tela

		System.out.println("**** TELA DO CARRINHO ****");
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));

		System.out.println(" ** ITENS DE TOTAIS ** ");

		System.out.println(Funcoes.removeTextoItemDeveolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));

		// Asser�oes Hamcrest

		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),
				is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()),
				is(esperado_subtotalProduto));

		assertThat(Funcoes.removeTextoItemDeveolveInt(carrinhoPage.obter_numeroItensTotal()),
				is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()),
				is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()), is(esperado_taxesTotal));

		// Asser�oes junit
		assertEquals(esperado_nomeProduto, carrinhoPage.obter_nomeProduto());
		assertEquals(esperado_precoProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		assertEquals(esperado_tamanhoProduto, carrinhoPage.obter_tamanhoProduto());
		assertEquals(esperado_corProduto, carrinhoPage.obter_corProduto());
		assertEquals(esperado_input_quantidadeProduto, Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()));
		assertEquals(esperado_subtotalProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));

		assertEquals(esperado_numeroItensTotal,
				Funcoes.removeTextoItemDeveolveInt(carrinhoPage.obter_numeroItensTotal()));
		assertEquals(esperado_subtotalTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subtotalProduto()));
		assertEquals(esperado_shippingTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		assertEquals(esperado_totalTaxExclTotal,
				Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		assertEquals(esperado_totalTaxIncTotal,
				Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxIncTotal()));
		assertEquals(esperado_taxesTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
	}

	CheckoutPage checkoutPage;

	@Test
	public void irParaCheckout_FreteMeioPagamentoEnderfecoListadosOk() {
		// Pr� condi��es

		// Produto dispon�vel no carrinho de compras
		irParaCarrinho_InformacoesPersistidas();

		// teste

		// clicar no bot�o
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();

		// preencher informa��es

		// validar informa��es na tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()),
				is(esperado_totalTaxIncTotal));
		// assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));

		checkoutPage.clicarBotaoContinueAddress();

		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, "tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);

		assertThat(encontrado_shippingValor_Double, is(esperado_shippingTotal));

		checkoutPage.clicar_botaoContinueShipping();

		// selecionar op��o "pay by check"

		checkoutPage.selecionar_RadioPayByCheck();

		// validar valor do cheque (amount)
		String encontrado_amountPayCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayCheck = Funcoes.removeTexto(encontrado_amountPayCheck, " (tax incl.)");
		Double encontrado_amountPayCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayCheck);

		assertThat(encontrado_amountPayCheck_Double, is(esperado_totalTaxIncTotal));

		// clicar na op��o "I agree"
		checkoutPage.selecionarCheckboxIAgree();

		assertTrue(checkoutPage.estaSelecionadoCheckboxIAgree());

	}

	@Test
	public void finalizarPedido_pedidofinalizadoComSucesso() {
		// pr� condi��es
		// checkout completamente conclu�do
		irParaCheckout_FreteMeioPagamentoEnderfecoListadosOk();

		// teste
		// clicar no bot�o para confirmar o pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmadoPedido();

		// validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		// assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(), is ("YOUR
		// ORDER IS CONFIRMED"));

		assertThat(pedidoPage.obter_email(), is("carlakdias@gmail.com "));

		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalProduto));

		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxIncTotal));

		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));

	}

}
