package com.example.rxjavaexamples

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Observable */
        Observable.create(ObservableOnSubscribe<String> { emitter ->
            emitter.onNext("Lunes")
            emitter.onNext("Martes")
            emitter.onNext("Miércoles")
            emitter.onComplete()
        })
            .subscribe(
                object : Observer<String> {
                    override fun onComplete() {
                        Log.i("Observable:", "onComplete")
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(t: String) {
                        Log.i("Observable:", "onNext: $t")
                    }

                    override fun onError(e: Throwable) {
                        Log.e("Observable:", "onError", e)
                    }
                }
            )

        /* Single */
//        Single.create(SingleOnSubscribe<String> { emitter ->
//            emitter.onSuccess("Single satisfactorio!!")
//        })
//            .subscribe(
//                object : SingleObserver<String> {
//                    override fun onSuccess(t: String) {
//                        TODO("not implemented")
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        TODO("not implemented")
//                    }
//                }
//            )

        /* Just + disposable*/
        val disposable = Observable.just("Lunes", "Martes", "Miércoles")
            .subscribe(
                { Log.i("Just:", "onNext: $it") },
                { Log.e("Just:", "onError", it) })

        compositeDisposable.add(
            Observable.just("Lunes", "Martes", "Miércoles")
                .subscribe(
                    { Log.i("JustCompDisposable:", "onNext: $it") },
                    { Log.e("JustCompDisposable:", "onError", it) })
        )


        /* Flowable */
        val flowable = Flowable.fromCallable({ 1 })

        //onBackpressureBuffer
        flowable
            .onBackpressureBuffer(
                16, //Tamaño buffer
                { /* acción */ },
                BackpressureOverflowStrategy.DROP_LATEST
            )
            .observeOn(Schedulers.computation())
            .subscribe(
                { /* onNext*/ },
                { /* onError*/ },
                { /* onComplete*/ }
            )

        //onBackpressureDrop
        flowable
            .onBackpressureDrop()
            .observeOn(Schedulers.computation())
            .subscribe()

        //onBackpressureLatest
        flowable
            .onBackpressureLatest()
            .observeOn(Schedulers.computation())
            .subscribe()

    }

    override fun onStop() {
        compositeDisposable.clear()
        super.onStop()
    }
}
