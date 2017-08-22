package com.ufrpe.feelingsbox.usuario.gui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.ufrpe.feelingsbox.R;
import com.ufrpe.feelingsbox.infra.GuiUtil;
import com.ufrpe.feelingsbox.infra.ValidacaoService;
import com.ufrpe.feelingsbox.redesocial.gui.ActHome;
import com.ufrpe.feelingsbox.usuario.usuarioservices.UsuarioService;

public class ActLogin extends AppCompatActivity {
    private EditText edtLogin, edtSenha;
    private UsuarioService usuarioService;
    private ValidacaoService validacaoService;
    private static final int DURACAO = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        this.encontrandoItens();
        this.animacaoTela();
    }

    private void encontrandoItens(){
        edtLogin = (EditText)findViewById(R.id.edtLogin);
        edtSenha = (EditText)findViewById(R.id.edtSenha);
    }

    private void animacaoTela(){
        try {
            YoYo.with(Techniques.FadeIn)
                    .duration(DURACAO)
                    .repeat(0)
                    .playOn(findViewById(R.id.mainLayoutLogin));
        }catch (Exception e){
            Log.d("animacaoTela()", "Erro na animação na tela de login.");
        }
    }

    public void cadastrarUsuario(View view){
        Intent it = new Intent(ActLogin.this, ActSignUp.class);
        startActivity(it);
    }

    public void efetuarLogin(View view){
        String login = edtLogin.getText().toString();
        String senha = edtSenha.getText().toString();
        validacaoService = new ValidacaoService(getApplicationContext());

        boolean vazio = false;
        if (validacaoService.isCampoVazio(senha)){
            edtSenha.requestFocus();
            edtSenha.setError("O campo senha está vazio.");
            vazio = true;
        }
        if (validacaoService.isCampoVazio(login)){
            edtLogin.requestFocus();
            edtLogin.setError("O campo login está vazio.");
            vazio = true;
        }

        if (!vazio) {
            this.chamarValidacao(login, senha);
        }
    }

    private void chamarValidacao(String login, String senha){
        usuarioService = new UsuarioService(getApplicationContext());
        if (!validacaoService.isEmail(login)) {
            try {
                usuarioService.logarNick(login, senha);
                irTelaHome();
            }
            catch (Exception e) {
                GuiUtil.myToast(this, "Login ou senha incorretos.");
            }
        }

        else {
            try {
                usuarioService.logarEmail(login, senha);
                irTelaHome();
            }
            catch (Exception e) {
                GuiUtil.myToast(this, "Login ou senha incorretos.");
            }
        }
    }

    private void irTelaHome(){
        Intent it = new Intent(ActLogin.this, ActHome.class);
        startActivity(it);
        finish();
    }
}