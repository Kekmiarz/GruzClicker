package com.gruzclicker
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {
    private val GAME_DATA_PREFS = "game_data"
    private val SCORE_KEY = "score"
    private val CLICK_MULTIPLIER_KEY = "click_multiplier"
    private val CEGLA_COUNT_KEY = "cegla_count"
    private val CEGLA_KOSZT_KEY = "cegla_koszt"
    private val CEGLA_MULTIPLIER_KEY = "cegla_multiplier"
    private val KATALIZATOR_STATE = "katalizator"
    private val CAR_LEVEL_KEY = "level_auta"
    private val BACKGROUND_LEVEL_KEY = "level_tla"

    private var score = 0
    private var otwarty = 0
    private var click_multiplier = 1
    private var cegla_multiplier = 1
    private var cegla_koszt = 50
    private var currentCarUpgradePrice = 2500
    private var currentBackgroundUpgradePrice = 5000
    private var ceglaCount = 0
    private var katalizatorState = 0
    private var currentScale = 1f
    private var currentCarScale = 1f
    private var maxScale = 1.3f
    private var scaleStep = 0.1f
    private var currentCarLevel = 1
    private var currentBackgroundLevel = 1
    private val scaleDownHandler = Handler(Looper.getMainLooper())

    private lateinit var carUpgrade: TextView
    private lateinit var scaleDownRunnable: Runnable
    private lateinit var scoreText: TextView
    private lateinit var carImage: ImageView
    private lateinit var button: Button
    private lateinit var katalizator: TextView
    private lateinit var cegla: TextView
    private lateinit var ceglaCounter: TextView
    private lateinit var menuLayout: ConstraintLayout
    private lateinit var katalizatorUpgrade: ImageView
    private lateinit var ceglaUpgrade:ImageView
    private lateinit var cheatButton:Button
    private lateinit var backgroundUpgrade:TextView
    private lateinit var backgroundImage: ImageView
    private val handler = Handler()
    private val updateScoreRunnable = object : Runnable {
        override fun run() {
            score += 1 * cegla_multiplier
            scoreText.text = "Punkty: $score"
            saveScore()
            handler.postDelayed(this, 1000)
        }
    }
    private fun createScaleDownRunnable(view: View) = Runnable {
        animateScale(view, 1f)  // Ustawia skalę na 1 (czyli przywraca do oryginalnego rozmiaru)
    }
    private fun animateScale(view:View, targetScale: Float) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", targetScale)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", targetScale)

        scaleX.duration = 100
        scaleY.duration = 100

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.start()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val carDrawables = arrayOfNulls<Drawable>(5)

        carDrawables[0] = ContextCompat.getDrawable(this, R.drawable.gruz1)
        carDrawables[1] = ContextCompat.getDrawable(this, R.drawable.gruz2)
        carDrawables[2] = ContextCompat.getDrawable(this, R.drawable.gruz3)
        carDrawables[3] = ContextCompat.getDrawable(this, R.drawable.gruz4)
        carDrawables[4] = ContextCompat.getDrawable(this, R.drawable.gruz5)

        val backgroundDrawables = arrayOfNulls<Drawable>(2)

        backgroundDrawables[0] = ContextCompat.getDrawable(this, R.drawable.tlo1)
        backgroundDrawables[1] = ContextCompat.getDrawable(this, R.drawable.tlo2)

        val sharedPreferences: SharedPreferences = getSharedPreferences(GAME_DATA_PREFS, MODE_PRIVATE)
        score = sharedPreferences.getInt(SCORE_KEY, 0)
        click_multiplier=sharedPreferences.getInt(CLICK_MULTIPLIER_KEY, 1)
        cegla_koszt = sharedPreferences.getInt(CEGLA_KOSZT_KEY, 50)
        cegla_multiplier = sharedPreferences.getInt(CEGLA_MULTIPLIER_KEY, 0)
        ceglaCount = sharedPreferences.getInt(CEGLA_COUNT_KEY, 0)
        katalizatorState = sharedPreferences.getInt(KATALIZATOR_STATE, 0)
        currentCarLevel = sharedPreferences.getInt(CAR_LEVEL_KEY, 1)
        currentBackgroundLevel = sharedPreferences.getInt(BACKGROUND_LEVEL_KEY, 1)

        scoreText = findViewById(R.id.scoreText)
        backgroundImage = findViewById(R.id.backgroundImage)
        backgroundImage.setImageDrawable(backgroundDrawables[currentBackgroundLevel-1])
        carImage = findViewById(R.id.carImage)
        carImage.setImageDrawable(carDrawables[currentCarLevel-1])
        button = findViewById<Button>(R.id.button)
        katalizator = findViewById<TextView>(R.id.katalizator)
        cegla = findViewById<TextView>(R.id.cegla)
        ceglaCounter = findViewById<TextView>(R.id.ceglaCounter)
        menuLayout = findViewById<ConstraintLayout>(R.id.menuLayout)
        katalizatorUpgrade = findViewById<ImageView>(R.id.katalizatorImage)
        ceglaUpgrade = findViewById<ImageView>(R.id.ceglaImage)
        cheatButton =findViewById<Button>(R.id.cheat)
        carUpgrade = findViewById<TextView>(R.id.carUpgrade)
        backgroundUpgrade = findViewById<TextView>(R.id.backgroundUpgrade)

        carUpgrade.setOnClickListener(){
            if(score>=currentCarUpgradePrice && currentCarLevel < carDrawables.size) {
                score-=currentCarUpgradePrice
                currentCarLevel += 1
                currentCarUpgradePrice += 2500
                click_multiplier += 25
                carImage.setImageDrawable(carDrawables[currentCarLevel - 1])
            }
        }
        backgroundUpgrade.setOnClickListener(){
            if(score>=currentBackgroundUpgradePrice && currentBackgroundLevel < backgroundDrawables.size) {
                score-=currentBackgroundUpgradePrice
                currentBackgroundLevel += 1
                currentBackgroundUpgradePrice += 5000
                click_multiplier += 75
                backgroundImage.setImageDrawable(backgroundDrawables[currentBackgroundLevel - 1])
            }
        }
        cheatButton.setOnClickListener(){
            score+=1000
        }
        if(katalizatorState==1){
            katalizator.visibility=View.GONE
        }
        if(katalizatorState==0){
            katalizatorUpgrade.visibility=View.GONE
        }
        if(ceglaCount==0){
            ceglaUpgrade.visibility=View.GONE
        }

        scoreText.text = "Punkty: $score"

        handler.post(updateScoreRunnable)
        scaleDownRunnable = Runnable {
            animateScale(carImage, 1f)
            currentCarScale = 1f
        }
        carImage.setOnClickListener {
            score += (1 * click_multiplier)

            scaleDownHandler.removeCallbacks(scaleDownRunnable)

            if (currentCarScale < maxScale) {
                currentCarScale += scaleStep
                if (currentCarScale > maxScale) currentCarScale = maxScale

                animateScale(carImage, currentCarScale)
            }
            scaleDownHandler.postDelayed(scaleDownRunnable, 200)
            scoreText.text = "Punkty: $score"
            saveScore()
        }
        ceglaCounter.text = "Koszt cegły: $cegla_koszt | Mnożnik na sekundę: x$cegla_multiplier"
        cegla.setOnClickListener{
            if(score>=cegla_koszt){
                scaleDownHandler.removeCallbacks(scaleDownRunnable)
                scaleDownHandler.postDelayed(scaleDownRunnable, 200)

                cegla_multiplier+=1
                score-=cegla_koszt
                cegla_koszt+=50
                ceglaCount++
                ceglaUpgrade.visibility=View.VISIBLE
                animateScale(ceglaUpgrade, 1.5f)
                scaleDownRunnable = Runnable {
                    animateScale(ceglaUpgrade, 1f)
                    currentScale = 1f
                }
                scaleDownHandler.removeCallbacks(scaleDownRunnable)
                scaleDownHandler.postDelayed(scaleDownRunnable, 200)
                scoreText.text = "Punkty: $score"
                ceglaCounter.text = "Koszt cegły: $cegla_koszt | Mnożnik na sekundę: x$cegla_multiplier"
                scaleDownRunnable = Runnable {
                    animateScale(carImage, 1f)
                    currentCarScale = 1f
                }
            }
        }
        katalizator.setOnClickListener {
            if (score >= 1000) {
                click_multiplier += 10
                score -= 1000
                katalizator.visibility=View.GONE
                katalizatorState=1
                scoreText.text = "Punkty: $score"
                katalizatorUpgrade.visibility=View.VISIBLE
                saveScore()
            }
        }
        button.setOnClickListener {
            if (otwarty == 0) {
                menuLayout.visibility = View.VISIBLE
                otwarty = 1
            } else {
                menuLayout.visibility = View.GONE
                otwarty = 0
            }
        }
    }

    // Zapisz wynik do SharedPreferences
    private fun saveScore() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(GAME_DATA_PREFS, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(SCORE_KEY, score)
        editor.putInt(CLICK_MULTIPLIER_KEY, click_multiplier)
        editor.putInt(CEGLA_KOSZT_KEY, cegla_koszt)
        editor.putInt(CEGLA_MULTIPLIER_KEY, cegla_multiplier)
        editor.putInt(CEGLA_COUNT_KEY, ceglaCount)
        editor.putInt(KATALIZATOR_STATE, katalizatorState)
        editor.putInt(CAR_LEVEL_KEY, currentCarLevel)
        editor.putInt(BACKGROUND_LEVEL_KEY, currentBackgroundLevel)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        saveScore()
    }
}
