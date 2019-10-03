package tech.linjiang.pandora.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.Pandora;
import tech.linjiang.pandora.cache.Content;
import tech.linjiang.pandora.cache.Summary;
import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.network.NetStateListener;
import tech.linjiang.pandora.ui.connector.SimpleOnActionExpandListener;
import tech.linjiang.pandora.ui.connector.SimpleOnQueryTextListener;
import tech.linjiang.pandora.ui.item.NetItem;
import tech.linjiang.pandora.ui.recyclerview.BaseItem;
import tech.linjiang.pandora.ui.recyclerview.UniversalAdapter;
import tech.linjiang.pandora.util.Config;
import tech.linjiang.pandora.util.NetFilter;
import tech.linjiang.pandora.util.SimpleTask;
import tech.linjiang.pandora.util.Utils;
import tech.linjiang.pandora.util.ViewKnife;

/**
 * Created by linjiang on 2018/6/22.
 */

public class NetFragment extends BaseListFragment implements Toolbar.OnMenuItemClickListener,
        CompoundButton.OnCheckedChangeListener, NetStateListener {

    private List<BaseItem> originData = new ArrayList<>();
    private List<BaseItem> tmpFilter = new ArrayList<>();

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getToolbar().setTitle(R.string.pd_name_network);
        Button button = new Button(getContext());
        button.setText("NetFilter");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetFilterDialog.create(getContext()).setOnChangeListener(
                        new NetFilterDialog.OnChangeListener() {
                    @Override
                    public void onChange() {
                        if (NetFilter.get().isEnable()){
                            filter();
                        }else{
                            loadData();
                        }
                    }
                }).show();
            }
        });

        getToolbar().getMenu().add(-1, R.id.pd_menu_id_0, 0, "")
                .setActionView(button)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_1, 1, "")
                .setActionView(new SwitchCompat(getContext()))
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_2, 2, R.string.pd_name_search)
                .setActionView(new SearchView(getContext()))
                .setIcon(R.drawable.pd_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_3, 3, R.string.pd_name_clear);
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_4, 4, "Filter 200");
        getToolbar().getMenu().add(-1, R.id.pd_menu_id_5, 5, "json");

        setSearchView();
        getToolbar().setOnMenuItemClickListener(this);
        SwitchCompat switchCompat = ((SwitchCompat) getToolbar()
                .getMenu().findItem(R.id.pd_menu_id_1).getActionView());
        switchCompat.setOnCheckedChangeListener(this);
        if (Config.isNetLogEnable()) {
            switchCompat.setChecked(true);
        } else {
            showOpenLogHint();
        }
        Pandora.get().getInterceptor().setListener(this);
        getAdapter().setListener(new UniversalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BaseItem item) {
                if (item instanceof NetItem) {
                    Bundle bundle = new Bundle();
                    bundle.putLong(PARAM1, ((Summary)item.data).id);
                    launch(NetSummaryFragment.class, bundle);
                }
            }
        });
        NetFilter.setup();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Pandora.get().getInterceptor().removeListener();
    }

    private void loadData() {
        hideError();
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, List<Summary>>() {
            @Override
            public List<Summary> doInBackground(Void[] params) {
                return Summary.queryList();
            }

            @Override
            public void onPostExecute(List<Summary> result) {
                hideLoading();
                if (Utils.isNotEmpty(result)) {
                    List<BaseItem> data = new ArrayList<>(result.size());
                    for (Summary summary : result) {
                        if (!NetFilter.get().filter(summary)){
                            data.add(new NetItem(summary));
                        }
                    }
                    getAdapter().setItems(data);

                    originData.clear();
                    originData.addAll(getAdapter().getItems());
                } else {
                    showError(null);
                }
            }
        }).execute();
    }

    private void clearData() {
        showLoading();
        new SimpleTask<>(new SimpleTask.Callback<Void, Void>() {
            @Override
            public Void doInBackground(Void[] params) {
                Summary.clear();
                Content.clear();
                return null;
            }

            @Override
            public void onPostExecute(Void result) {
                getAdapter().clearItems();
                hideLoading();
                showError(null);
            }
        }).execute();
    }

    private void setSearchView() {
        MenuItem menuItem = getToolbar().getMenu().findItem(R.id.pd_menu_id_2);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        searchView.setQueryHint(ViewKnife.getString(R.string.pd_net_search_hint));
        searchView.setOnQueryTextListener(new SimpleOnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                closeSoftInput();
                search(query);
                return true;
            }
        });
        SimpleOnActionExpandListener.bind(menuItem, new SimpleOnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                loadData();
                return true;
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.pd_menu_id_3) {
            if (!Config.isNetLogEnable()) {
                return false;
            }
            clearData();
        }
        if (item.getItemId() == R.id.pd_menu_id_4) {
            NetFilter.filter200 = !NetFilter.filter200;
            filter();
        }
        if (item.getItemId() == R.id.pd_menu_id_5) {
            return true;
        }
        closeSoftInput();
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Config.setNetLogEnable(isChecked);
        if (isChecked) {
            loadData();
        } else {
            showOpenLogHint();
        }
    }

    private void showOpenLogHint() {
        getAdapter().clearItems();
        showError(ViewKnife.getString(R.string.pd_please_open_net_log));
    }

    @Override
    public void onRequestStart(long id) {
        refreshSingleData(true, id);
    }

    @Override
    public void onRequestEnd(long id) {
        refreshSingleData(false, id);
    }

    private void search(String condition) {
        tmpFilter.clear();
        if (TextUtils.isEmpty(condition)) {
            loadData();
            return;
        }
        if (Utils.isNotEmpty(originData)) {
            for (int i = originData.size() - 1; i >= 0; i--) {
                if (originData.get(i) instanceof NetItem) {
                    String url = ((Summary)originData.get(i).data).url;
                    if (url.contains(condition)) {
                        tmpFilter.add(originData.get(i));
                    }
                }
            }
            getAdapter().setItems(tmpFilter);
        }
    }

    private void filter() {
        NetFilter filter = NetFilter.get();
        if (filter == null || !NetFilter.isEnable()){
            return;
        }
        tmpFilter.clear();
        for (int i = originData.size() - 1; i >= 0; i--) {
            if (originData.get(i) instanceof NetItem) {
                Summary summary = ((Summary)originData.get(i).data);
                if (!filter.filter(summary)){
                    tmpFilter.add(originData.get(i));
                }
            }
        }
        getAdapter().setItems(tmpFilter);
    }

    private void refreshSingleData(final boolean isNew, final long id) {
        new SimpleTask<>(new SimpleTask.Callback<Void, Summary>() {
            @Override
            public Summary doInBackground(Void[] params) {
                return Summary.query(id);
            }

            @Override
            public void onPostExecute(Summary result) {
                hideLoading();
                if (result == null||NetFilter.get().filter(result)) {
                    return;
                }
                if (!isNew) {
                    for (int i = 0; i < getAdapter().getItems().size(); i++) {
                        if (getAdapter().getItems().get(i) instanceof NetItem) {
                            if (((Summary) getAdapter().getItems().get(i).data).id == result.id) {
                                getAdapter().getItems().get(i).data = result;
                                getAdapter().notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                } else {
                    getAdapter().insertItem(new NetItem(result), 0);
                }
                originData.clear();
                originData.addAll(getAdapter().getItems());
            }
        }).execute();
    }
}
