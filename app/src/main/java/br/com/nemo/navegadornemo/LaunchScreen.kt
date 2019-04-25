package br.com.nemo.navegadornemo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

class LaunchScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Indica o layout a qual será inicializado nesta classe, no caso a tela que será a SplashScreen
        setContentView(R.layout.activity_launch_screen)
        //Método a qual indicamos uma ação especifica e que ela ocorrerá após um determinado tempo no caso:
        Handler().postDelayed({
            //Inicializaremos a activity principal e finalizaremos a atual da splash após 2 segundos
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
        window.decorView.apply {
            //Ocultando a barra de navegação utilizada em alguns aparelhos e a barra de status
            //Atributo da classe View recebe o valor para esconder a barra de navegação ou oculpar toda a tela
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
            // SYSTEM_UI_FLAG_FULLSCREEN está disponível apenas no Android 4.1 e superior, mas como
            // uma regra geral, você deve projetar seu aplicativo para ocultar a barra de status sempre que
            // esconder a barra de navegação também.
        }

    }
}
