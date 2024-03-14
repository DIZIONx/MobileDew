package com.example.lab20;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText initialInvestmentEditText;
    private EditText annualInterestRateEditText;
    private EditText numberOfYearsEditText;
    private TextView resultTextView;

    private WebView webView;

    private Handler handler = new Handler();
    private Runnable updateDataRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setContentView(R.layout.activity_main);

        initialInvestmentEditText = findViewById(R.id.initialInvestmentEditText);
        annualInterestRateEditText = findViewById(R.id.annualInterestRateEditText);
        numberOfYearsEditText = findViewById(R.id.numberOfYearsEditText);
        resultTextView = findViewById(R.id.resultTextView);
        Button calculateButton = findViewById(R.id.calculateButton);
        Button openWebsiteButton = findViewById(R.id.openWebsiteButton);
        webView = findViewById(R.id.webView);
        configureWebView();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateInvestment();
            }
        });

        openWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInvestmentWebsite();
            }
        });
        // Создаем и запускаем поток для обновления данных каждые 5 секунд
        updateDataRunnable = new Runnable() {
            @Override
            public void run() {
                calculateInvestment();
                handler.postDelayed(this, 5000); // Обновляем каждые 5 секунд
            }
        };
        handler.post(updateDataRunnable);

    }
    private void calculateInvestment() {
        // Извлекаем данные из полей ввода
        String initialInvestmentStr = initialInvestmentEditText.getText().toString();
        String annualInterestRateStr = annualInterestRateEditText.getText().toString();
        String numberOfYearsStr = numberOfYearsEditText.getText().toString();

        // Проверка на пустоту
        if (initialInvestmentStr.isEmpty() || annualInterestRateStr.isEmpty() || numberOfYearsStr.isEmpty()) {
            resultTextView.setText("Заполните все поля ввода.");
            return;
        }

        // Преобразование строки в число
        double initialInvestment = Double.parseDouble(initialInvestmentStr);
        double annualInterestRate = Double.parseDouble(annualInterestRateStr);
        int numberOfYears = Integer.parseInt(numberOfYearsStr);

        double futureValue = initialInvestment * Math.pow((1 + annualInterestRate), numberOfYears);
        double profit = futureValue - initialInvestment;

        String resultMessage = String.format("Через %d лет ваша инвестиция вырастет до %.2f рублей. Заработок: %.2f рублей.",
                numberOfYears, futureValue, profit);

        resultTextView.setText(resultMessage);
    }
    private void openInvestmentWebsite() {
        // Задайте URL своего предпочтительного сайта по инвестициям
        String investmentWebsiteUrl = "https://quote.rbc.ru";

        // Создайте интент для открытия браузера с указанным URL
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(investmentWebsiteUrl));
        startActivity(browserIntent);
    }

    private void configureWebView() {
        // Замените на идентификатор своего WebView
        WebView webView = findViewById(R.id.webView);

        // Настройки WebView
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Установка WebViewClient для обработки ошибок загрузки
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                resultTextView.setText("Ошибка загрузки страницы");
            }
        });

        // Очищаем кэш и устанавливаем режим без кэша
        webView.clearCache(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Укажите URL вашего предпочтительного сайта по инвестициямzz
        webView.loadUrl("https://quote.rbc.ru");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Отменяем обновление данных при уничтожении активности
        handler.removeCallbacks(updateDataRunnable);
    }
}