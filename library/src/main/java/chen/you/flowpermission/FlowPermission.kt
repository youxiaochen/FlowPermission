package chen.you.flowpermission

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList

/**
 * FlowPermission Build.VERSION_CODES.M权限申请
 * author: you : 2021/11/14
 */
class FlowPermission private constructor(private val fragmentManager: FragmentManager) {

    private val permissionFragment: FlowPermissionsFragment by lazy {
        val fragment = fragmentManager.findFragmentByTag(TAG)
        if (fragment is FlowPermissionsFragment) fragment else {
            FlowPermissionsFragment().also {
//                Log.d("FlowPermission", "FlowPermissionsFragment new Instance...")
                fragmentManager.beginTransaction().add(it, TAG).commitNow()
            }
        }
    }

    constructor(activity: FragmentActivity) : this(activity.supportFragmentManager)

    constructor(fragment: Fragment) : this(fragment.childFragmentManager)

    fun isGranted(permission: String): Boolean = permissionFragment.isGranted(permission)

    fun isRevoked(permission: String): Boolean = permissionFragment.isRevoked(permission)

    fun requestEach(vararg permissions: String): Flow<Permission> = permissionFragment.requestPermissions(setOf(*permissions))

    fun request(vararg permissions: String): Flow<Boolean> = flow {
        requestEach(*permissions).toList().forEach {
            if (!it.granted) {
                emit(false); return@flow
            }
        }
        emit(true)
    }

    companion object {
        private const val TAG = "FragmentTag_FlowPermission"
    }
}