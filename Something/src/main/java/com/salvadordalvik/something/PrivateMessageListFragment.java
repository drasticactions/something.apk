package com.salvadordalvik.something;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.salvadordalvik.fastlibrary.alert.FastAlert;
import com.salvadordalvik.fastlibrary.list.FastItem;
import com.salvadordalvik.fastlibrary.list.SectionFastAdapter;
import com.salvadordalvik.something.list.PrivateMessageFolderItem;
import com.salvadordalvik.something.list.PrivateMessageItem;
import com.salvadordalvik.something.list.ThreadItem;
import com.salvadordalvik.something.request.PMDeleteRequest;
import com.salvadordalvik.something.request.PrivateMessageListRequest;

/**
 * Created by matthewshepard on 2/7/14.
 */
public class PrivateMessageListFragment extends SomeFragment implements Response.ErrorListener, Response.Listener<PrivateMessageListRequest.PMListResult> {
    private static final int REQUEST_REPLY = 120;
    private ListView pmList;
    private SectionFastAdapter adapter;

    private int folderId = 0;

    public PrivateMessageListFragment() {
        super(R.layout.generic_listview, R.menu.pm_list);
    }

    @Override
    public void viewCreated(View frag, Bundle savedInstanceState) {
        pmList = (ListView) frag.findViewById(R.id.listview);
        adapter = new SectionFastAdapter(this, 2);
        pmList.setAdapter(adapter);
        pmList.setOnItemClickListener(adapter);

        registerForContextMenu(pmList);
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefreshIfStale();
    }

    @Override
    public void refreshData(boolean pullToRefresh, boolean staleRefresh) {
        queueRequest(new PrivateMessageListRequest(folderId, false, this, this));
    }

    @Override
    public void onResponse(PrivateMessageListRequest.PMListResult pmListResult) {
        adapter.replaceSection(0, pmListResult.folders);
        adapter.replaceSection(1, pmListResult.messages);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        FastAlert.error(getActivity(), getView(), getSafeString(R.string.loading_failed));
    }

    public void showFolder(int id) {
        folderId = id;
        startRefresh();
    }

    public void onPaneObscured() {

    }

    public void onPaneRevealed() {

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        FastItem item = adapter.getItem(info.position);
        if (item instanceof PrivateMessageItem) {
            getActivity().getMenuInflater().inflate(R.menu.pm_item, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_delete:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                FastItem pm = adapter.getItem(info.position);
                if(pm != null){
                    queueRequest(new PMDeleteRequest(folderId, deleteListener, null, pm.getId()));
                    return true;
                }
        }
        return super.onContextItemSelected(item);
    }

    public Response.Listener deleteListener = new Response.Listener() {
        @Override
        public void onResponse(Object response) {
            FastAlert.notice(PrivateMessageListFragment.this, R.string.pm_deleted);
            startRefresh();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_newpm:
                startActivityForResult(
                        new Intent(getActivity(), ReplyActivity.class)
                                .putExtra("pm_id", ReplyFragment.NEW_PM)
                                .putExtra("reply_type", ReplyFragment.TYPE_PM),
                        REQUEST_REPLY
                );
                return true;
            case R.id.menu_refresh:
                startRefresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_REPLY && resultCode > 0){
            FastAlert.notice(this, R.string.reply_sent_pm);
            startRefresh();
        }
    }
}
