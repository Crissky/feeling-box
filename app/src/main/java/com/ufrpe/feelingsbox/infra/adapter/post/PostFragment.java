package com.ufrpe.feelingsbox.infra.adapter.post;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ufrpe.feelingsbox.R;
import com.ufrpe.feelingsbox.redesocial.dominio.ActEnum;
import com.ufrpe.feelingsbox.redesocial.dominio.Post;
import com.ufrpe.feelingsbox.redesocial.dominio.Sessao;
import com.ufrpe.feelingsbox.redesocial.gui.ActCriarPostComentario;
import com.ufrpe.feelingsbox.redesocial.gui.ActPerfilPost;
import com.ufrpe.feelingsbox.redesocial.gui.ActPost;
import com.ufrpe.feelingsbox.redesocial.redesocialservices.RedeServices;
import com.ufrpe.feelingsbox.usuario.dominio.Usuario;
import com.ufrpe.feelingsbox.usuario.usuarioservices.UsuarioService;

import java.util.ArrayList;
import java.util.List;

import static com.ufrpe.feelingsbox.redesocial.dominio.BundleEnum.COMENTARIO;
import static com.ufrpe.feelingsbox.redesocial.dominio.BundleEnum.ID_POST;
import static com.ufrpe.feelingsbox.redesocial.dominio.BundleEnum.TAB;

/**
 * Classe que exibirá @see {@link android.support.v7.widget.CardView} com os dados do @see {@link Post}
 * em um @see {@link PostRecyclerAdapter}.
 */

public class PostFragment extends Fragment implements RecyclerViewOnClickListenerhack{
    private RecyclerView mRecyclerView;
    private List<Post> mList;
    private Sessao sessao = Sessao.getInstancia();
    private Usuario usuarioDonoTela;
    private final int TAB_HOME = 0;
    private final int TAB_FAVORITO = 1;
    private final int TAB_RECOMENDADO = 2;

    //Setando o RecyclerView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            /*@Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();

                if(mList.size() == mLinearLayoutManager.findLastCompletelyVisibleItemPosition() + 1){
                    List<Post> listaAux = redeServices.exibirPosts();

                    for (int i = 0; i < listaAux.size(); i++){
                        adapter.addListItem(listaAux.get(i), mList.size() );

                    }

                }
            }*/
        });

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        RedeServices redeServices = new RedeServices(getActivity());
        usuarioDonoTela = sessao.getUltimoUsuario();
        int tab = 0;
        Bundle bundle = getArguments();
        if(bundle != null){
            tab = bundle.getInt(TAB.getValor());
        }
        if(usuarioDonoTela != null){
            mList = redeServices.exibirPosts(usuarioDonoTela.getId());
        } else if (tab == TAB_HOME){
            mList = redeServices.exibirPosts();
        } else if (tab == TAB_FAVORITO){
            mList = redeServices.getPostsFavoritos(sessao.getUsuarioLogado().getId());
        } else if (tab == TAB_RECOMENDADO){
            if (redeServices.isNovato(sessao.getUsuarioLogado().getId())){
                mList = redeServices.postsFiltradosComentarios();
            }
            else {
                mList = redeServices.gerarPostsTagAproximada(sessao.getUsuarioLogado().getId());
            }
        } else {
            mList = new ArrayList<>();
        }

        PostRecyclerAdapter adapter = new PostRecyclerAdapter(getActivity(), mList);
        adapter.setRecyclerViewOnClickListenerhack(this);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    /**
     * Método que executa ação ao clicar em um elemento da @see {@link android.support.v7.widget.RecyclerView.ViewHolder}
     * @param view
     * @param position
     */

    @Override
    public void onClickListener(View view, int position) {
        Intent intent;
        UsuarioService usuarioService = new UsuarioService(view.getContext());
        Usuario usuarioSelecionado = usuarioService.buscarUsuario(mList.get(position).getIdUsuario());
        switch (view.getId()){
            case R.id.ivUser:
                if(sessao.getUltimoHistorico() == ActEnum.ACT_PERFIL_POST && usuarioDonoTela.getId() == usuarioSelecionado.getId()){
                    break;
                }
                sessao.addUsuario(usuarioSelecionado);
                intent = new Intent(view.getContext(), ActPerfilPost.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btnComentar:
                sessao.addModo(COMENTARIO);
                intent = new Intent(view.getContext(), ActCriarPostComentario.class);
                intent.putExtra(ID_POST.getValor(), mList.get(position).getId());
                startActivity(intent);
                getActivity().finish();
                break;
            case -1:
                sessao.addPost(mList.get(position));
                sessao.addUsuario(usuarioSelecionado);
                intent = new Intent(view.getContext(), ActPost.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }

    //Click longo
    @Override
    public void onLongPressClickListener(View view, int position) {
        PostRecyclerAdapter adapter = (PostRecyclerAdapter) mRecyclerView.getAdapter();
        adapter.removeListItem(position);
    }
}