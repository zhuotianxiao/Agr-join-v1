package cn.fundview.app.activity.msg;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.List;

import cn.fundview.R;
import cn.fundview.app.action.msg.FundviewInforHistoryAction;
import cn.fundview.app.action.msg.InitFundviewInforListAction;
import cn.fundview.app.domain.model.FundviewInfor;
import cn.fundview.app.tool.DateTimeUtil;
import cn.fundview.app.tool.adapter.RecyclerViewAdapter;
import cn.fundview.app.view.AsyncTaskCompleteListener;
import cn.fundview.app.view.common.pullrefresh.PullToRefreshBase;
import cn.fundview.app.view.common.pullrefresh.PullToRefreshRecyclerView;
import me.itangqi.greendao.DaoMaster;
import me.itangqi.greendao.DaoSession;
import me.itangqi.greendao.NoteDao;

public class FundViewInforListActivity extends AppCompatActivity implements AsyncTaskCompleteListener,RecyclerViewAdapter.MyRecyclerViewItemOnClickListener {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

    private PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private ImageButton mBackImageButton;

    private int id;//当前页面中存储的最小id,用于查看历史
    private int size;//每次查询的条数
    private List<FundviewInfor> dataSource;//recyclerview 数据源
    private Handler handler;
    private int lastVisibleItem = -1;



    private SQLiteDatabase db;
    private EditText editText;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_findview_infor_list);

        // 官方推荐将获取 DaoMaster 对象的方法放到 Application 层，这样将避免多次创建生成 Session 对象
        setupDatabase();
        // 获取 NoteDao 对象
        getNoteDao();

        int id = getIntent().getIntExtra("id", 0);//设置未读的最大的id
        size = 1;//标示在资讯列表页中显示的资讯条数
        handler = new Handler();

        initViews();
        attachEvents();
        initData(id,size);
    }

    /**
     * 读取数据库 或 请求网络 完成处理
     * @param requestCode  请求码
     * @param responseCode 响应码 0 第一次请求 1后续的加载历史数据
     * @param msg          返回的消息,可以携带异步任务额执行结果
     */
    @Override
    public void complete(int requestCode, int responseCode, Object msg) {

        if(null != msg) {

            List<FundviewInfor> fundviewInforList = (List<FundviewInfor>)msg;
            if(null != fundviewInforList && fundviewInforList.size() > 0) {

                id = fundviewInforList.get(0).getId() - 1;
            }

            if(responseCode == 0) {

                dataSource = fundviewInforList;

                RecyclerViewAdapter viewAdapter = new RecyclerViewAdapter<FundviewInfor>(dataSource, this, R.layout.fundview_info_item_layout) {
                    @Override
                    public void bindViewHolder(MyViewHolder viewHolder, FundviewInfor item) {

                        viewHolder.setText(R.id.receive_time, DateTimeUtil.getRelativeTimeSinceNow(item.getUpdateDate()));
                        viewHolder.setText(R.id.title, item.getTitle());
                        viewHolder.setText(R.id.publish_time, DateTimeUtil.formatTime(item.getUpdateDate()));
                        viewHolder.setImageByUrl(R.id.logo, item.getLogo(), R.mipmap.zx_moren, R.mipmap.zx_moren);
                        viewHolder.setText(R.id.summary, item.getIntroduction());
                    }

                };
                viewAdapter.setClickListener(this);
                this.mRecyclerView.setAdapter(viewAdapter);
            }else if(responseCode == 1) {

                if(fundviewInforList != null && fundviewInforList.size() > 0) {

                    id = fundviewInforList.get(0).getId()-1;
                    ((RecyclerViewAdapter)this.mRecyclerView.getAdapter()).insertData(fundviewInforList);
                    fundviewInforList.addAll(dataSource);
                    dataSource = fundviewInforList;
                }

                mPullToRefreshRecyclerView.onPullDownRefreshComplete();

            }
        }

    }

    /**
     * 初始化view
     */
    private void initViews() {

        this.mToolbar = (Toolbar) this.findViewById(R.id.toolBar);
        this.mBackImageButton = (ImageButton) (this.mToolbar.findViewById(R.id.backImageBtn));
        this.mPullToRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.pullRecyclerView);//new PullToRefreshWebView(this);
        this.mRecyclerView = this.mPullToRefreshRecyclerView.getRecyclerView();
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setHasFixedSize(true);
    }

    /**
     * 绑定事件
     */
    private void attachEvents() {

        this.mBackImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

                loadHistoryData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

            }
        });
    }

    /**
     * 进入该 activity 后 第一次加载数据
     * @param id 未读的最小的id
     * @param size 查询的条数
     */
    private void initData(int id,int size) {

        new InitFundviewInforListAction(this,id,size,this);
    }

    private void loadHistoryData() {

        new FundviewInforHistoryAction(this,id,size, this);
    }

    @Override
    public void onClick(View view, int position) {

        Intent intent = new Intent(this, FundViewInforDetailActivity.class);
        intent.putExtra("title",dataSource.get(position).getTitle());
        intent.putExtra("id",dataSource.get(position).getId());
        intent.putExtra("updateDate",dataSource.get(position).getUpdateDate()+"");
        startActivity(intent);
    }

    private void setupDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    private NoteDao getNoteDao() {
        return daoSession.getNoteDao();
    }


}
