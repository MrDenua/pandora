package tech.linjiang.pandora;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tech.linjiang.pandora.core.R;
import tech.linjiang.pandora.function.IFunc;
import tech.linjiang.pandora.inspector.CurInfoView;
import tech.linjiang.pandora.inspector.GridLineView;
import tech.linjiang.pandora.ui.Dispatcher;
import tech.linjiang.pandora.ui.connector.Type;
import tech.linjiang.pandora.ui.view.FuncView;
import tech.linjiang.pandora.util.Utils;

/**
 * Created by linjiang on 2019/3/4.
 */

class FuncController implements Application.ActivityLifecycleCallbacks, FuncView.OnItemClickListener {

    private final FuncView funcView;
    private final CurInfoView curInfoView;
    private final GridLineView gridLineView;
    private int activeCount;
    private final List<IFunc> functions = new ArrayList<>();

    FuncController(Application app) {
        funcView = new FuncView(app);
        funcView.setOnItemClickListener(this);
        curInfoView = new CurInfoView(app);
        gridLineView = new GridLineView(app);
        app.registerActivityLifecycleCallbacks(this);
        addDefaultFunctions();
    }

    void addFunc(IFunc func) {
        functions.add(func);
        funcView.addItem(func.getIcon(), func.getName());
    }

    void open() {
        if (funcView.isVisible()) {
            boolean succeed = funcView.open();
            if (!succeed) {
                Dispatcher.start(Utils.getContext(), Type.PERMISSION);
            }
        }
    }

    void close() {
        funcView.close();
    }

    private void showOverlay() {
        funcView.setVisibility(View.VISIBLE);
        curInfoView.setVisibility(View.VISIBLE);
        gridLineView.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        funcView.setVisibility(View.GONE);
        curInfoView.setVisibility(View.GONE);
        gridLineView.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int index) {
        functions.get(index).onClick();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        activeCount++;
        if (activeCount == 1) {
            showOverlay();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof Dispatcher) {
            hideOverlay();
        }
        curInfoView.updateText(activity.getClass().getName());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof Dispatcher) {
            if (activeCount > 0) {
                showOverlay();
            }
        }
        curInfoView.updateText(null);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activeCount--;
        if (activeCount <= 0) {
            hideOverlay();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void addDefaultFunctions() {
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_network;
            }

            @Override
            public String getName() {
                return "network";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.NET);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_disk;
            }

            @Override
            public String getName() {
                return "sandbox";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.FILE);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_select;
            }

            @Override
            public String getName() {
                return "select";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.SELECT);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_bug;
            }

            @Override
            public String getName() {
                return "crash";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.BUG);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_layer;
            }

            @Override
            public String getName() {
                return "hierarchy";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.HIERARCHY);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_ruler;
            }

            @Override
            public String getName() {
                return "baseline";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.BASELINE);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_map;
            }

            @Override
            public String getName() {
                return "navigate";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.ROUTE);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_history;
            }

            @Override
            public String getName() {
                return "history";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.HISTORY);
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_windows;
            }

            @Override
            public String getName() {
                return "activity";
            }

            @Override
            public void onClick() {
                curInfoView.toggle();
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_grid;
            }

            @Override
            public String getName() {
                return "gridLine";
            }

            @Override
            public void onClick() {
                gridLineView.toggle();
            }
        });
        addFunc(new IFunc() {
            @Override
            public int getIcon() {
                return R.drawable.pd_config;
            }

            @Override
            public String getName() {
                return "config";
            }

            @Override
            public void onClick() {
                Dispatcher.start(Utils.getContext(), Type.CONFIG);
            }
        });
    }
}
