package sv.edu.ues.fia.eisi.controlremoto;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton btnEnviar;
    ImageButton btnStop;
    ImageButton btnIzquierdo;
    ImageButton btnDerecho;
    Button btnDesconectar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // referenciamos los controles
        btnEnviar = findViewById(R.id.btnEnviar);
        btnStop = findViewById(R.id.btnStop);
        btnIzquierdo = findViewById(R.id.btnIzquierdo);
        btnDerecho = findViewById(R.id.btnDerecho);
        btnDesconectar = findViewById(R.id.btnDesconectar);

        // eventos
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnIzquierdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDerecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
