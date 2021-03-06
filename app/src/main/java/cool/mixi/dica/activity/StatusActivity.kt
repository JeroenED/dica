package cool.mixi.dica.activity

import android.os.Bundle
import cool.mixi.dica.App
import cool.mixi.dica.R
import cool.mixi.dica.bean.Consts
import cool.mixi.dica.bean.Status
import cool.mixi.dica.util.ApiService
import cool.mixi.dica.util.IStatusDataSource
import cool.mixi.dica.util.StatusTimeline
import cool.mixi.dica.util.dLog
import kotlinx.android.synthetic.main.activity_status.*
import retrofit2.Call
import java.util.*

class StatusActivity: BaseActivity(), IStatusDataSource {

    private var statusId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)
        processIntent()
    }

    private fun processIntent(){
        if(intent == null) {return}
        statusId = intent.getIntExtra(Consts.ID_STATUS, 0)
        if(statusId == 0){
            App.instance.toast(getString(R.string.status_not_found))
            finish()
            return
        }

        dLog("StatusId: ${statusId}")
        stl = StatusTimeline(this, rv_statuses_list, home_srl, this).init()
        home_srl.setOnRefreshListener {
            stl?.loadNewest(this)
        }

        stl?.loadNewest(this)
    }

    override fun loaded(data: List<Status>) {
        if(data.isEmpty()){
            App.instance.toast(getString(R.string.status_not_exists))
            finish()
            return
        }

        stl?.allLoaded = true
        Collections.reverse(data)
        stl?.clear()
        stl?.addAll(data!!)
        rv_statuses_list.adapter.notifyDataSetChanged()
    }

    override fun sourceOld(): Call<List<Status>>? {
        return null
    }

    override fun sourceNew(): Call<List<Status>>? {
        return ApiService.create().statusShow(statusId!!, 1)
    }
}