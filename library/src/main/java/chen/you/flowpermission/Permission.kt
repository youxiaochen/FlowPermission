package chen.you.flowpermission

/**
 * Permission Build.VERSION_CODES.M权限申请
 * author: you : 2021/11/14
 */
class Permission(val name: String, val granted: Boolean, val shouldShowReqPermRationale: Boolean = false) {

    override fun equals(o: Any?): Boolean = o === this || (o is Permission && this.name == o.name &&
            this.granted == o.granted && this.shouldShowReqPermRationale == o.shouldShowReqPermRationale)


    override fun hashCode(): Int {
        val result = 31 * name.hashCode() + if (granted) 1 else 0
        return 31 * result + if (shouldShowReqPermRationale) 1 else 0
    }

    override fun toString(): String = "FlowPermission{name='$name'\', " +
            "granted=$granted, shouldShowReqPermRationale=$shouldShowReqPermRationale}"
}