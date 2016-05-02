package co.moonmonkeylabs.realmsearchview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;

/**
 * A View that has a search bar with a results view for displaying typeahead results in a list that
 * is backed by a Realm.
 */
public class RealmSearchView extends LinearLayout  implements SearchView.OnQueryTextListener{

    private RealmRecyclerView realmRecyclerView;
    private SearchView searchBar;
    private RealmSearchAdapter adapter;
    private Context context;
    private AttributeSet attrs;

    private boolean addFooterOnIdle;

    public RealmSearchView(Context context) {
        super(context);
        init(context, null);
    }

    public RealmSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RealmSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.realm_search_view, this);
        setOrientation(VERTICAL);

         this.context = context;
        this.attrs = attrs;
        realmRecyclerView = (RealmRecyclerView) findViewById(R.id.realm_recycler_view);

    }

    private Handler handler = null;

    public void setSearchBar(SearchView text) {
        searchBar = text;
        initAttrs(context, attrs);
        searchBar.setOnQueryTextListener(this);

    }
    
        @Override
    public boolean onQueryTextChange(String query) {
        adapter.filter(query);
        addFooterHandler(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        addFooterHandler(query);
        return true;
    }

    private void addFooterHandler(final String search) {
        if (!addFooterOnIdle) {
            return;
        }
        if (handler != null) {
            return;
        }

        adapter.removeFooter();
        handler = new Handler();
        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (search.equals(searchBar.getText().toString())) {
                            adapter.addFooter();
                        }
                        handler = null;
                    }
                },
                300);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
         TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.RealmSearchView);

        int hintTextResId = typedArray.getResourceId(
                R.styleable.RealmSearchView_rsvHint,
                R.string.rsv_default_search_hint);

        searchBar.setQueryHint(getResources().getString(hintTextResId));


        addFooterOnIdle = typedArray.getBoolean(R.styleable.RealmSearchView_rsvAddFooter, false);

        typedArray.recycle();
    }

    public void setAdapter(RealmSearchAdapter adapter) {
        this.adapter = adapter;
        realmRecyclerView.setAdapter(adapter);
        this.adapter.filter("");
    }

    public String getSearchBarText() {
        return searchBar.getText().toString();
    }

   
}
