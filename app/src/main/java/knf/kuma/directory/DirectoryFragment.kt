package knf.kuma.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import knf.kuma.BottomFragment
import knf.kuma.R
import knf.kuma.commons.EAHelper
import knf.kuma.commons.PrefsUtil
import knf.kuma.commons.safeDismiss
import knf.kuma.commons.showSnackbar
import knf.kuma.database.CacheDB
import knf.kuma.directory.viewholders.DirMainFragmentHolder

class DirectoryFragment : BottomFragment() {
    private var fragmentHolder: DirMainFragmentHolder? = null
    private var snackbar: Snackbar? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ViewModelProviders.of(this).get(DirectoryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_directory, container, false)
        fragmentHolder = DirMainFragmentHolder(view, childFragmentManager)
        if (!PrefsUtil.isDirectoryFinished) {
            snackbar = activity?.findViewById<View>(R.id.root)?.showSnackbar("Creando directorio...", Snackbar.LENGTH_INDEFINITE)
            CacheDB.INSTANCE.animeDAO().allListCount.observe(this, Observer {
                try {
                    snackbar?.setText("Agregados... $it")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            DirectoryService.getLiveStatus().observe(this, Observer {
                when (it) {
                    DirectoryService.STATE_VERIFYING ->
                        snackbar?.setText("Verificando directorio...")
                    DirectoryService.STATE_INTERRUPTED,
                    DirectoryService.STATE_FINISHED ->
                        snackbar?.safeDismiss()
                }
            })
        }
        EAHelper.enter1("D")
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        snackbar?.safeDismiss()
    }

    fun onChangeOrder() {
        fragmentHolder?.onChangeOrder()
    }

    override fun onReselect() {
        EAHelper.enter1("D")
        fragmentHolder?.onReselect()
    }

    companion object {

        fun get(): DirectoryFragment {
            return DirectoryFragment()
        }
    }
}
