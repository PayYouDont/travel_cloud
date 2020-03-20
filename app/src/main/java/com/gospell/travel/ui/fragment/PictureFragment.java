package com.gospell.travel.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gospell.travel.Constants;
import com.gospell.travel.R;
import com.gospell.travel.common.annotation.RootView;
import com.gospell.travel.common.annotation.ViewById;
import com.gospell.travel.common.base.BaseFragment;
import com.gospell.travel.common.util.BitmapUtil;
import com.gospell.travel.common.util.DateUtil;
import com.gospell.travel.common.util.HttpUtil;
import com.gospell.travel.entity.MediaBean;
import com.gospell.travel.entity.RequestPage;
import com.gospell.travel.ui.device.DeviceAccessFragment;
import com.gospell.travel.ui.view.CardView;
import com.gospell.travel.ui.view.FuncMenuView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PictureFragment extends BaseFragment implements BackListener {
    @RootView(R.layout.picture_fragment)
    private View root;
    @ViewById(R.id.list_refresh)
    private SwipeRefreshLayout refreshLayout;
    @ViewById(R.id.list_scroll)
    private ScrollView listScroll;
    @ViewById(R.id.list_layout)
    private LinearLayout listLayout;
    @ViewById(R.id.function_menu_layout)
    private FuncMenuView funcMenuView;
    private String baseUrl = "http://"+Constants.HTTP_SERVER_IP+":"+Constants.HTTP_SERVER_PORT+"/picture/";
    //正在加载第几个CardView中的图片
    //private int cardViewIndex;
    private boolean destroy = false;
    private boolean isEdit = false;
    private List<MediaBean> selectedMediaBeanList;
    //预计加载
    private int expectedLoadCount = 30;
    private int currentShowCount;
    private RequestPage requestPage;
    private AlertDialog.Builder alertDialog;
    private JSONArray thumbnailUrlArr;
    private Handler handler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1){//返回图片信息列表
                //cardViewIndex++;
                loadThumbnail (msg.obj.toString ());
            }else if (msg.arg1 == 2){//返回缩略图
                MediaBean mediaBean = (MediaBean) msg.obj;
                createCardView (mediaBean);
                currentShowCount++;
                if(currentShowCount==requestPage.getLoadCount ()){
                    getThumbnailList();
                }
            }else if(msg.arg1 == 3){//返回图片
                Bitmap bitmap = (Bitmap) msg.obj;
                ImageView imageView = new ImageView (getContext ());
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams (params);
                imageView.setImageBitmap (bitmap);
                if(alertDialog==null){
                    alertDialog = new AlertDialog.Builder (getContext ());
                    alertDialog.setPositiveButton ("下载",(dialog, which) -> {
                        System.out.println ("下载");
                    });
                    alertDialog.setNegativeButton ("关闭",(dialog, which) -> dialog.dismiss ());
                }
                alertDialog.setView (imageView);
                //dialog.setContentView (imageView);
                alertDialog.show ();
            }
        }
    };
    @Override
    protected void onCreateView() {
        //cardViewIndex = 0;
        destroy = false;
        selectedMediaBeanList = new ArrayList<> ();
        requestPage = new RequestPage ();
        //下拉刷新
        refreshLayout.setOnRefreshListener (() -> {
            //initData();
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        });
        funcMenuView.download (v -> selectedMediaBeanList.forEach (mediaBean -> {
            System.out.println ("下载："+mediaBean);
            //getPicture (mediaBean.getPath ());
        }));
        listScroll.setOnScrollChangeListener ((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

        });
        //上拉加载
        /*listRecyclerView.addOnScrollListener (new RecyclerView.OnScrollListener () {
            //newState:（1、SCROLL_STATE_IDLE：滑动停止时 2、SCROLL_STATE_DRAGGING：触摸拖动时 、3：SCROLL_STATE_SETTLING：拖动到最后，不受外界影响）
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged (recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled (recyclerView, dx, dy);
            }
        });*/
        getThumbnailList();
    }
    /**
    * @Author peiyongdong
    * @Description ( 初始化数据，向http服务端请求图片列表 )
    * @Date 15:04 2020/3/17
    * @Param []
    * @return void
    **/
    private void getThumbnailList(){
        //...picture/page?date=2020-3-13&index=1&size=10
        String listUrl = baseUrl+"page?date="
                + DateUtil.formatDate (requestPage.getLoadDate (),"yyyy-MM-dd")
                +"&index="+requestPage.getPageIndex ()
                +"&size="+requestPage.getPageSize ();
        HttpUtil.get (listUrl,call -> call.enqueue (new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace ();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body ().string ();
                Message message = new Message ();
                message.obj = json;
                message.arg1 = 1;
                handler.sendMessage (message);
            }
        }));
    }
    /**
     * @Author peiyongdong
     * @Description ( 解析服务端返回的图片路径列表 )
     * @Date 16:24 2020/3/10
     * @Param [json]
     * @return void
     **/
    private void loadThumbnail(String json){
        try {
            //int index = cardViewIndex;
            JSONObject object = new JSONObject (json);
            boolean success = object.getBoolean ("success");
            if(success){
                thumbnailUrlArr = object.getJSONArray ("data");
                if(DateUtil.getTimeDistance (requestPage.getLoadDate (),Constants.createDate)>0){//数据已加载到了用户创建的日期，则停止加载
                    Log.d (getClass ().getName (),"没有数据了");
                    return;
                }
                if(thumbnailUrlArr.length ()==0){//当天没有数据
                    Date date = requestPage.getLoadDate ();
                    requestPage.setLoadDate (DateUtil.getBeforeDay (date));
                    getThumbnailList ();
                    return;
                }else {
                    int loadCount = requestPage.getLoadCount ()+thumbnailUrlArr.length ();
                    requestPage.setLoadCount (loadCount);
                    if(requestPage.getLoadCount ()>expectedLoadCount){//如果请求加载数据量大于预期加载量，暂时停止加载，直到上拉后再加载
                        return;
                    }
                    if(thumbnailUrlArr.length ()<requestPage.getPageSize ()){//小于每页显示的总数说明当天数据已经加载完毕，日期设置为前一天
                        Date date = requestPage.getLoadDate ();
                        requestPage.setLoadDate (DateUtil.getBeforeDay (date));
                    }else {//否则请求的页数+1
                        int pageIndex = requestPage.getPageIndex ();
                        requestPage.setPageSize (pageIndex++);
                    }
                    loadThumbnail();
                }
            }
        }catch (JSONException e){
            e.printStackTrace ();
        }
    }
    private void loadThumbnail() throws JSONException{
        for(int i=0;i<thumbnailUrlArr.length ();i++){
            //...picture/thumbnail?width=100&height=100&path=xx/xx.jpg
            String imgUrl = baseUrl+"thumbnail?width=80&height=80&path="+Uri.encode (thumbnailUrlArr.getString (i));
            //getThumbnail (imgUrl,index);
            getThumbnail (imgUrl);
        }
    }
    /**
    * @Author peiyongdong
    * @Description ( 根据图片路径向http服务端获取缩略图 )
    * @Date 16:27 2020/3/10
    * @Param [imgUrl]
    * @return void
    **/
    private void getThumbnail(String imgUrl){
        HttpUtil.get (imgUrl, call -> call.enqueue (new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace ();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body ();
                Bitmap bitmap = BitmapUtil.stringToBitmap (body.string ());
                MediaBean mediaBean = createMediaBean (bitmap,imgUrl);
                Message message = new Message ();
                message.obj = mediaBean;
                message.arg1 = 2;
                handler.sendMessage (message);
            }
        }));
    }
    /**
    * @Author peiyongdong
    * @Description ( 获取完整图片 )
    * @Date 15:14 2020/3/17
    * @Param [imgUrl]
    * @return void
    **/
    private void getPicture(String imgUrl){
        HttpUtil.get (imgUrl, call -> call.enqueue (new Callback () {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace ();
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                ResponseBody body = response.body ();
                Bitmap bitmap = BitmapFactory.decodeStream (body.byteStream ());
                Message message = new Message ();
                message.obj = bitmap;
                message.arg1 = 3;
                handler.sendMessage (message);
            }
        }));
    }
    /**
     * @Author peiyongdong
     * @Description ( 根据MediaBean创建或更新cardView )
     * @Date 16:25 2020/3/10
     * @Param [mediaBean]
     * @return void
     **/
    private void createCardView(MediaBean bean){
        CardView cardView;
        View view = listLayout.findViewWithTag ("loadDate:"+requestPage.getLoadDate ());
        if(view!=null&&view instanceof CardView && !destroy){
            cardView = (CardView)view;
            cardView.getMediaBeanList ().add (bean);
            cardView.updateView ();
        }else {
            List<MediaBean> mediaBeans = new ArrayList<> ();
            mediaBeans.add (bean);
            cardView = new CardView (getContext (),mediaBeans);
            cardView.setTag ("loadDate:"+requestPage.getLoadDate ());
            cardView.setOnItemClickListener ((itemView, mediaBean) -> {
                String imgUrl = baseUrl+"get?path="+mediaBean.getPath ();
                getPicture (imgUrl);
            });
            cardView.setOnItemOnLongClickListener ((viewHolder, mediaBean) -> setEditStatus(true));
            cardView.setOnItemCheckedChangeListener ((mediaBean, isChecked) -> {
                if(isChecked){
                    selectedMediaBeanList.add (mediaBean);
                }else {
                    selectedMediaBeanList.remove (mediaBean);
                }
            });
            listLayout.addView (cardView);
        }
    }
    /**
    * @Author peiyongdong
    * @Description ( 创建MediaBean )
    * @Date 16:27 2020/3/10
    * @Param [bitmap, url]
    * @return com.gospell.travel.entity.MediaBean
    **/
    private MediaBean createMediaBean(Bitmap bitmap,String url){
        MediaBean bean = new MediaBean ();
        bean.setBitmap (bitmap);
        bean.setSize (bitmap.getByteCount ());
        bean.setCreateTime (new Date ());
        bean.setPath (url.substring (url.lastIndexOf ("path=")+5));
        String urlStr = Uri.decode (url);
        bean.setDisplayName (urlStr.substring (urlStr.lastIndexOf ("\\")+1));
        return bean;
    }
    /**
    * @Author peiyongdong
    * @Description ( 设置编辑状态 )
    * @Date 15:10 2020/3/12
    * @Param [isEdit]
    * @return void
    **/
    private void setEditStatus(boolean isEdit){
        this.isEdit = isEdit;
        if(isEdit){
            funcMenuView.setVisibility (View.VISIBLE);
        }else {
            funcMenuView.setVisibility (View.GONE);
        }
        for(int i=0;i<listLayout.getChildCount ();i++){
            View view = listLayout.getChildAt (i);
            if(view instanceof CardView){
                CardView cardView = (CardView) view;
                cardView.setEdit (isEdit);
            }
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        destroy = true;
    }
    @Override
    public void onBack() {
        if(isEdit){
            setEditStatus(false);
        }else {
            getFragmentManager ().beginTransaction ().replace (R.id.nav_host_fragment,new DeviceAccessFragment ()).commit ();
        }
    }
}
