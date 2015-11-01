package com.example.blanke.recyclerviewtext;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.blanke.recyclerviewtext.data.ImageData;
import com.example.blanke.recyclerviewtext.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private String[] mDatas;
    private ThreadPoolManager mThreadPoolManager;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initView();
        initData();
    }

    private void initData() {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new MyAdapter());
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mThreadPoolManager = new ThreadPoolManager(2, ThreadPoolManager.Type.LIFO);
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);

    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_recyclerview, null));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
//            holder.textView.setText(mDatas[position]);
//            View textView = holder.textView;
//            ViewGroup.LayoutParams lp = textView.getLayoutParams();
//            lp.height = (int) ((Math.random() * 10 + 2) * 10);
//            textView.setLayoutParams(lp);
            Log.d("imageload1", "onBindViewHolder（）：" + position);
            System.out.println(position);
            final String url = ImageData.imageThumbUrls[position];
            holder.imageView.setTag(url);
            holder.imageView.setImageBitmap(null);
            mThreadPoolManager.addTask(new Runnable() {

                @Override
                public void run() {
                    Log.d("imageload1", "开始下载：" + url);
                    final Bitmap img = ImageUtils.getChche(MainActivity.this, url);
                    Log.d("imageload1", "下载完成：" + url);
                    String nowUrl = (String) holder.imageView.getTag();
                    if (nowUrl.equals(url)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("imageload1", "设置图片 " + position);
                                holder.imageView.setImageBitmap(img);
                            }
                        });
                    } else {
                        Log.d("imageload1", "图片设置错误 " + position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return ImageData.imageThumbUrls.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public MyViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.item_recyclerview_image);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
