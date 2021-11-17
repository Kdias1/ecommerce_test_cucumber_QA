package steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

//import static org.junit.Assert.assertThat;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.common.io.Files;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import pages.HomePage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.ProdutoPage;

public class ComparProdutosSteps {

	private static WebDriver driver;
	private HomePage homePage = new HomePage(driver);

	@Before
	public static void inicializar() {
		System.setProperty("webdriver.chrome.driver", "C:\\webdrivers\\chromedriver\\95\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	}

	@Dado("que estou na pagina inicial")
	public void que_estou_na_pagina_inicial() {
		homePage.CarregarPaginaInicial();
		assertThat(homePage.obterTituloPagina(), is("Loja de Teste"));

	}

	@Quando("nao estou logado")
	public void nao_estou_logado() {
		assertThat(homePage.estaLogado(), is(false));
	}

	@Entao("visualizo {int} produtos disponiveis")
	public void visualizo_produtos_disponiveis(Integer int1) {
		assertThat(homePage.contarProdutos(), is(int1));
	}

	@Entao("carrinho esta zerado")
	public void carrinho_esta_zerado() {
		assertThat(homePage.obterQuantidadeProdutosNoCarrinho(), is(0));
	}

	LoginPage loginPage;

	@Quando("estou logado")
	public void estou_logado() {
		// clicar no bot�o sign in na home page
		loginPage = homePage.clicarBotaoSignIn();

		// preencher usu�rio e senha
		loginPage.preencherEmail("carlakdias@gmail.com");
		loginPage.preencherPassword("Javatest1234*");

		// clicar no bot�o sign in para logar"
		loginPage.clicarBotaoSignIn();

		// validar se o usu�rio ta logado de fato

		assertThat(homePage.estaLogado("Carla Dias"), is(true));

		homePage.CarregarPaginaInicial();
	}

	ProdutoPage produtoPage;
	String nomeProduto_HomePage;
	String precoProduto_HomePage;

	String nomeProduto_ProdutoPage;
	String precoProduto_ProdutoPage;

	@Quando("seleciono um produto na posicao {int}")

	public void seleciono_um_produto_na_posicao(Integer indice) {

		nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

	}

	
	/*@Quando("o nome do produto na tela principal  e na tela produto eh  {string}")
	public void o_nome_do_produto_na_tela_principal_e_na_tela_produto_eh(String nomeProduto) {
		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto.toUpperCase()));
		assertThat(nomeProduto_ProdutoPage.toUpperCase(), is(nomeProduto.toUpperCase()));

	}*/
	
	@Quando("o nome do produto na tela principal  e na tela produto eh {string}")
	
	public void o_nome_do_produto_na_tela_principal_e_na_tela_produto_eh(String nomeProduto) {
		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto.toUpperCase()));
		assertThat(nomeProduto_ProdutoPage.toUpperCase(), is(nomeProduto.toUpperCase()));
	  
	}
	
	@Quando("preco do produto na tela principal e na tela produto eh {string}")
	public void preco_do_produto_na_tela_principal_e_na_tela_produto_eh(String precoProduto) {
		assertThat(precoProduto_HomePage, is(precoProduto.toUpperCase()));
		assertThat(precoProduto_ProdutoPage, is(precoProduto.toUpperCase()));
	}

	ModalProdutoPage modalProdutoPage;

	/*@Quando("adiciono o produto no carrinho com tamanho {string} cor {string} e quantidade {int}")
	public void adiciono_o_produto_no_carrinho_com_tamanho_cor_e_quantidade(String tamanhoProduto, String corProduto,
			Integer quantidadeProduto) {

		

	}*/
	
	@Quando("adiciono o produto no carrinho com tamanho {string} cor {string}  e quantidade {int}")
	public void adiciono_o_produto_no_carrinho_com_tamanho_cor_e_quantidade(String tamanhoProduto, String corProduto,
			Integer quantidadeProduto) {
		
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);
		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista: " + listaOpcoes.size());

		// Selecionar cor
		if (!corProduto.equals("N/A"))
		produtoPage.selecionarCorPreta();
		

		// selecionar quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// adicionar carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();

		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));
		
		
		
		
	  
	}

	@Então("o produto aparece na confirmacao com nome {string} preco {string} tamanho {string} cor {string} e quantidade {int}")
	public void o_produto_aparece_na_confirmacao_com_nome_preco_tamanho_cor_e_quantidade(String nomeProduto,
			String precoProduto, String tamanhoProduto, String corProduto, Integer quantidadeProduto) {
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));

		Double precoProdutoDoubleEncontrado = Double
				.parseDouble(modalProdutoPage.obterPrecoProduto().replace("$", " "));
		Double precoProdutoDoubleEsperado = Double.parseDouble(precoProduto.replace("$", " "));

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		if(!corProduto.equals("N/A"))
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

		String subtotalString = modalProdutoPage.obterSubtotal();
		subtotalString = subtotalString.replace("$", " ");
		Double subtotalEncontrado = Double.parseDouble(subtotalString);

		Double subtotalCalculadoEsperado = quantidadeProduto * precoProdutoDoubleEsperado;

		assertThat(subtotalEncontrado, is(subtotalCalculadoEsperado));

	}

	@After (order = 1)
	public void capturarTela (Scenario scenario) {
		var camera = (TakesScreenshot) driver; // TakesScreenshot camera = (TakeScreenshot) driver
		
		File capturaDeTela = camera.getScreenshotAs(OutputType.FILE);
		System.out.println(scenario.getId());
		
		String scenarioId = scenario.getId().substring(scenario.getId().lastIndexOf(".feature:") + 9);
		
		String nomeArquivo = "resources/screenshots/" + scenario.getName() + "_" +scenarioId + "_" + scenario.getStatus() + ".png";
		System.out.println(nomeArquivo);
		
		try {
			Files.move(capturaDeTela, new File (nomeArquivo));
					
		
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@After (order = 0)
	public static void finalizar() {
		driver.quit();
	}

}
