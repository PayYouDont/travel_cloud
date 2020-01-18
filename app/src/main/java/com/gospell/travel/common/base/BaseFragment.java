package com.gospell.travel.common.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.util.ReflectUtil;
import com.gospell.travel.helper.BackHandlerHelper;
import com.gospell.travel.helper.FragmentBackHandler;

import org.litepal.util.LogUtil;

import lombok.Getter;

@Getter
public abstract class BaseFragment extends Fragment{
    private View root;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
        initRootView ();
        initViewByAnnotation();
        onCreateView();
        return root;
    }
    abstract protected void onCreateView();

    private void initRootView(){
        ReflectUtil.initFieldByAnnotation (getClass (),RootView.class,(annotation, field) -> {
            try {
                RootView rootView = (RootView)annotation;
                if(rootView.value ()==-1){
                    field.set (this,inflater.inflate (getId (getActivity (),field.getName ()), container, false));
                }else{
                    field.set (this,inflater.inflate (rootView.value (), container, false));
                }
                root = (View) field.get (this);
            }catch (IllegalAccessException e) {
                LogUtil.e (getClass ().getName (), e);
            }
        });
    }

    private void initViewByAnnotation() {
        ReflectUtil.initFieldByAnnotation (getClass (),ViewById.class,(annotation, field) -> {
            ViewById viewById = (ViewById)annotation;
            try {
                if (viewById.value () == -1) {
                    field.set (this, root.findViewById (getId (getActivity (), field.getName ())));
                } else {
                    field.set (this, root.findViewById (viewById.value ()));
                }
            } catch (IllegalAccessException e) {
                LogUtil.e (getClass ().getName (), e);
            }
        });
    }

    private int getId(Context context, String resName) {
        return context.getResources ().getIdentifier (resName, "id", context.getPackageName ());
    }
}
