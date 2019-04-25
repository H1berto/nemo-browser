package br.com.nemo.navegadornemo

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    // Variavel global para a string de busca digitada
    lateinit var busca: String
    //Listener para o menu de navegação, para o evento de seleção de cada um
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        //Quando o atributo itemId do item
        when (item.itemId) {
            //for igual ao botão home
            R.id.navigation_home -> {
                //chamada do metodo para acessar a pagina de home
                acessarHomePage()
                // retorna true ao listener
                return@OnNavigationItemSelectedListener true

            }
            //for igual ao botão de voltar
            R.id.navigation_voltar -> {
                //se for possivel voltar a pagina anterior a qual ja tinha sido acessada
                if (myWeb.canGoBack()) {
                    //retorna a pagina anterior
                    myWeb.goBack()
                } else {
                    //senão, exibi uma mensagem
                    mostraMensagem("Sem histórico disponível")
                }
                // retorna true ao listener
                return@OnNavigationItemSelectedListener true
            }
            //for igual ao botão de avancar
            R.id.navigation_avancar -> {
                //se for possivel avançar a pagina posterior a qual ja tinha sido acessada,
                if (myWeb.canGoForward()) {
                    //avança a pagina
                    myWeb.goForward()
                } else {
                    //senão, exibi uma mensagem
                    mostraMensagem("Sem histórico disponível")

                }
                // retorna true ao listener 
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Indica o layout a qual será inicializado nesta classe, no caso a tela principál do navegador
        setContentView(R.layout.activity_main)
        //Indica o listener criado para o tratamento dos botões para a NavView
        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        //Chamada do metodo de configuração do WebView
        configureWebView()
        //Chamada do metodo de acesso a pagina de home do navegador
        acessarHomePage()

        //Evento de click do botão refresh
        btn_refresh.setOnClickListener {
            //Exibi uma mensagem
            mostraMensagem("Recarregando Página...")
            //Recarrega a pagina atual
            myWeb.reload()
        }

        //Evento de click do edt da url
        edt_url.setOnClickListener {
            //Seleciona o texto inteiro do edt
            edt_url.selectAll()
        }

        //Evento de click do Enter do teclado
        edt_url.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            //Se o codigo da tecla for igual ao Enter
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Chamada do metodo para acessar uma pagina
                acessarPagina()
                return@OnKeyListener true
            }
            false
        })


    }

    //configurando o web view
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {

        myWeb.settings.javaScriptEnabled = true
        myWeb.settings.allowFileAccess = true
        myWeb.settings.javaScriptCanOpenWindowsAutomatically = true

        // Enable zooming in web view
        myWeb.settings.setSupportZoom(true)
        myWeb.settings.builtInZoomControls = true
        myWeb.settings.displayZoomControls = true

        myWeb.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

                return false

            }
            //Metodo para tratar da inicialização das paginas
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                setarUrl()
            }
            //Metodo para tratamento de erros das paginas
            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                //exibir mensagem
                mostraMensagem("Endereço não encontrado, realizando busca no google")
                //Chamada do metodo de busca no Google
                buscarGoogle(busca)

            }

        }
    }

    //função para acessar páginas
    fun acessarPagina() {
        //se o edt da url não estiver vazio
        if (edt_url.text.isNotEmpty()) {
            //armazena o valor da url em uma variavel local e na variavel global busca
            busca = edt_url.text.toString()
            var endereco = edt_url.text.toString()
            //constante a qual armazenará a url final após o tratamento da string endereco digitado
            val url: String
            //tratamento do valor recebido na variavel endereco
            //quando
            endereco = when {
                //o valor da variavel endereco, em caixa baixa, conter www. e conter .com, endereco recebe seu proprio valor
                endereco.toLowerCase().contains("www.") && endereco.toLowerCase().contains(".com") -> endereco
                //o valor da variavel endereco, em caixa baixa, conter somente o .com, endereco recebe www. + seu proprio valor
                endereco.toLowerCase().contains(".com") -> "www.$endereco"
                //o valor da variavel endereco, em caixa baixa, conter somente o www. , endereco recebe seu proprio valor + .com
                endereco.toLowerCase().contains("www.") -> "$endereco.com"
                //senão, endereco recebe www.+seu proprio valor+.com
                else -> "www.$endereco.com"
            }
            //tratamento do valor a ser recebido na variavel url
            //quando
            url = when {
                //o valor da variavel endereco, em caixa baixa, conter https. ou conter .http, url recebe o valor de endereco
                endereco.toLowerCase().contains("https://") || endereco.toLowerCase().contains("http://") -> endereco
                //senão,a url recebe https:// + o valor de endereco
                else -> "https://$endereco"
            }

            //tratamento de erro ao acessar pagina
            //tentamos acessar a url e setamos a url no edt
            try {
                myWeb.loadUrl(url)
                setarUrl()
            } catch
                //caso surja um erro em relaçao a string de busca, nos fazemos a busca no google
                (e: Exception) {
                buscarGoogle(busca)
            }

        }
    }

    //função para acessar a homepage
    private fun acessarHomePage() {
        val home = "https://www.google.com"
        myWeb.loadUrl(home)
        setarUrl()
    }

    //função para colocar a url no edt url
    private fun setarUrl() {
        val url: String = myWeb.url
        edt_url.setText(url, TextView.BufferType.EDITABLE)

    }

    //função para mostrar mensagens
    private fun mostraMensagem(mensagem: String) {

        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }

    //para buscar no google
    private fun buscarGoogle(busca: String) {

        myWeb.loadUrl("https://www.google.com.br/search?q=$busca&hl=pt-br")
    }


}
