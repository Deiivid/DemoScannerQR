package com.example.demoscannerqr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val MY_PERMISSION_CAMERA = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            solicitarPermisos()
        }
        else{
            agregarEventoBoton()
            Toast.makeText(applicationContext,"Permisos ya concedidos",Toast.LENGTH_LONG).show()
        }

    }

    fun solicitarPermisos(){
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            //solicitar el permiso
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.CAMERA),MY_PERMISSION_CAMERA)
        }
        else{
            agregarEventoBoton()
            Toast.makeText(applicationContext,"Permisos concedidos anteriormente",Toast.LENGTH_LONG).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MY_PERMISSION_CAMERA->{
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    agregarEventoBoton()
                    Toast.makeText(applicationContext,"Permisos concedidos",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(applicationContext,"Permisos denegados",Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    fun agregarEventoBoton(){
        btnEscanear.isEnabled = true
        btnEscanear.setOnClickListener {
            IntentIntegrator(this)
                .setPrompt("Enfoca un Qr Válido")
                .initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result != null){
            if(result.contents == null){
                Toast.makeText(applicationContext,"Cancelado",Toast.LENGTH_LONG).show()
            }
            else{
                var contenido = result.contents
                if(contenido.startsWith("http")){
                    mostrarFragment(1,contenido)
                }else if(contenido.contains("VCARD")){
                    mostrarFragment(2,contenido)
                }
                Toast.makeText(applicationContext,"Contenido $contenido",Toast.LENGTH_LONG).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun mostrarFragment(opcion:Int,datos:String){
        lateinit var newFragment: Fragment
        when(opcion){
            1->{//sitio web
                newFragment = WebFragment.newInstance(datos)
            }
            2->{
                newFragment = ContactoFragment.newInstance(datos)
            }
            else->{
                Toast.makeText(applicationContext,"Código no válido",Toast.LENGTH_SHORT).show()
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor,newFragment)
        transaction.addToBackStack(null)

        Handler().postDelayed({
            transaction.commit()
        },1000)

    }


}