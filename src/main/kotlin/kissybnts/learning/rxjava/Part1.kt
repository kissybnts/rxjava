package kissybnts.learning.rxjava

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

internal class Part1 {
    /**
     * Flowable / Subscriber のサンプル関数
     */
    fun list11() {
        val flowable = Flowable.create(FlowableOnSubscribe<String> { e ->
            e ?: return@FlowableOnSubscribe

            val datas = arrayOf("Hello world", "こんにちは世界")

            for (data: String in datas) {
                if (e.isCancelled) {
                    return@FlowableOnSubscribe
                }
                e.onNext(data)
            }

            e.onComplete()
        }, BackpressureStrategy.BUFFER)

        flowable.observeOn(Schedulers.computation()).subscribe(object : Subscriber<String> {
            // the object to request the number of data and to release the subscribe
            lateinit private var subscription: Subscription

            // the function to be used when started to subscribe
            override fun onSubscribe(s: Subscription?) {
                // to keep the Subscription in this Subscriber
                this.subscription = s ?: throw IllegalArgumentException("subscription is null")
                // to request next data
                this.subscription.request(1)
            }

            // the function to be used when received data
            override fun onNext(t: String) {
                println("${Thread.currentThread().name}: $t")

                // to request next data
                this.subscription.request(1)
            }

            // the function to be used when received the notification of completed
            override fun onComplete() {
                println("${Thread.currentThread().name} is completed")
            }

            // the function to be used when received the notification of errors
            override fun onError(t: Throwable?) {
                t?.printStackTrace()
            }
        })
    }

    /**
     * Observable / Observer のサンプル関数
     */
    fun list18() {
        val observable = Observable.create(ObservableOnSubscribe<String> { e ->
            val datas = arrayOf("Hello world using Observable", "こんにちは世界、Observableを使って")

            for (data in datas) {
                if (e.isDisposed) {
                    return@ObservableOnSubscribe
                }

                e.onNext(data)
            }

            e.onComplete()
        })

        observable.observeOn(Schedulers.computation())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) { }

                    override fun onNext(t: String) {
                        println("${Thread.currentThread().name}: $t")
                    }

                    override fun onComplete() {
                        println("${Thread.currentThread().name}: 完了しました")
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }
                })
        Thread.sleep(2000)
    }
}