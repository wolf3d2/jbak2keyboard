package com.jbak2.JbakKeyboard;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jbak2.JbakKeyboard.IKeyboard.Lang;
import com.jbak2.JbakKeyboard.UpdateDownloader.VersionDiff;

public class UpdVocabActivity extends Activity
{
    ListView m_list;
    UpdateDownloader m_updDownloader;
    View m_emptyView;
    TextView m_emptyViewText;
    Vector<VersionDiff> m_arUpdates;
    UpdAdapter m_updAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        View v = getLayoutInflater().inflate(R.layout.pref_view, null);
        m_updDownloader = new UpdateDownloader();
        m_list = (ListView)v.findViewById(android.R.id.list);
        View emptyView = v.findViewById(R.id.top_item);
        m_emptyViewText = (TextView)emptyView.findViewById(R.id.text);
        m_emptyViewText.setText(R.string.upd_check);
        m_list.setEmptyView(emptyView);
        m_updAdapter = new UpdAdapter();
        m_list.setAdapter(m_updAdapter);
        m_list.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
                {
                    updateVocab(m_arUpdates.get(pos));
                }
            }
        );
        
        setContentView(v);
        checkForUpdates();
        Ads.show(this,1);
        super.onCreate(savedInstanceState);
    }
    st.UniObserver m_updEndCallback = new st.UniObserver()
    {
        @Override
        public int OnObserver(Object param1, Object param2)
        {
            m_arUpdates = (Vector<UpdateDownloader.VersionDiff>)param1;
            showUpdates();
            return 0;
        }
    };
    void checkForUpdates()
    {
        m_updDownloader.getVocabUpdates(m_updEndCallback,true);
    }
    void showUpdates()
    {
        if(m_arUpdates==null)
        {
            m_emptyViewText.setText(R.string.upd_get_error);
        }
        else if(m_arUpdates.size()==0)
        {
            m_emptyViewText.setText(R.string.upd_no_updates);
        }
        m_updAdapter.notifyDataSetChanged();
    }
    void updateVocab(VersionDiff vd)
    {
        UpdateDownloader.updateVocab(vd, this,new st.UniObserver()
        {
            @Override
            public int OnObserver(Object param1, Object param2)
            {
                m_updDownloader.getVocabUpdates(m_updEndCallback, false);
                return 0;
            }
        });
    }
    class UpdAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            if(m_arUpdates==null)
                return 0;
            return m_arUpdates.size();
        }

        @Override
        public Object getItem(int arg0)
        {
            return m_arUpdates.get(arg0);
        }

        @Override
        public long getItemId(int arg0)
        {
            return 0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup arg2)
        {
            if(convertView==null)
            {
                convertView = getLayoutInflater().inflate(R.layout.two_line_item, null);
            }
            VersionDiff vd = (VersionDiff)getItem(pos);
            setVocabView(vd, convertView);
            return convertView;
        }
        void setVocabView(VersionDiff vd,View v)
        {
            String lang = new Lang(0, vd.newVer.name).getName(getApplicationContext());
            ((TextView)v.findViewById(R.id.text)).setText(lang);
            ((TextView)v.findViewById(R.id.size)).setText(UpdateDownloader.fmtFileSize(getApplicationContext(), vd.newVer.size));
            String desc = st.STR_NULL;
            if(vd.oldVer==null)
            {
                desc=getString(R.string.upd_new_vocab);
            }
            else
            {
                desc=getString(R.string.upd_vocab_version_update)+vd.newVer.ver;
            }
            ((TextView)v.findViewById(R.id.desc)).setText(desc);
        }
    }
}
