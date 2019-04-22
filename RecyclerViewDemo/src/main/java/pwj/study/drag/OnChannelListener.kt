package pwj.study.drag

interface OnChannelListener {
    abstract fun onItemMove(starPos: Int, endPos: Int)
    abstract fun onFinish()
}