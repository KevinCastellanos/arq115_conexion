package sv.edu.ues.fia.eisi.controlremoto;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageButton btnEnviar;
    ImageButton btnStop;
    ImageButton btnIzquierdo;
    ImageButton btnDerecho;
    Button btnDesconectar;
    EditText edtTextoOut;
    TextView tvtMensaje;

    // ----------------------------------------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // identificador unico de servicio = SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // string para la dirección MAC
    private static String address = null;

    // ----------------------------------------------

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // metodo que permite la interaccion con arduino
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg){
                if (msg.what == handlerState) {
                    // interacción de los datos de ingreso

                    char MyCaracter = (char) msg.obj;

                    if (MyCaracter == 's') {
                        tvtMensaje.setText("MENSAJE STOP");
                    }

                    if (MyCaracter == 'i') {
                        tvtMensaje.setText("MENSAJE IZQUIERDA");
                    }

                    if (MyCaracter == 'd') {
                        tvtMensaje.setText("MENSAJE DERECHA");
                    }
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get bluetooth adapter
        verificarEstadoBT();

        // referenciamos los controles
        btnEnviar = findViewById(R.id.btnEnviar);
        btnStop = findViewById(R.id.btnStop);
        btnIzquierdo = findViewById(R.id.btnIzquierdo);
        btnDerecho = findViewById(R.id.btnDerecho);
        btnDesconectar = findViewById(R.id.btnDesconectar);
        edtTextoOut = findViewById(R.id.edtTextoSalida);
        tvtMensaje = findViewById(R.id.texto);


        // eventos
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String GetDat =edtTextoOut.getText().toString();
                tvtMensaje.setText(GetDat);

                // enviando data por el puerto serial
                MyConexionBT.write(GetDat);
            }
        });

        btnIzquierdo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("I");
            }
        });

        btnDerecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("D");
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("D");
            }
        });

        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btSocket != null) {
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "+" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        // crea una conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        // setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(this, "La creación del socket fallo", Toast.LENGTH_SHORT).show();
        }

        // establece la conexión la conexión con el socket Bluetooth
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                Toast.makeText(this, "+"+e2, Toast.LENGTH_SHORT).show();
            }
        }

        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            // cuando se sale de  la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e) {
            Toast.makeText(this, "No se pudo cerrar la conexion", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * comprueba que el dispositivo bluetooth este disponible
    * y solicita que este activa si esta desactivado
    * */
    private void verificarEstadoBT() {
        if (btAdapter ==  null) {
            Toast.makeText(this, "el dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if(btAdapter.isEnabled()) {

            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    // crea la clase que permite crear evento de conexion
    private class ConnectedThread extends Thread {
        // declaramos la variable
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        private ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] byte_in = new byte[1];
            // se mantiene en modo de escucha para determinar el ingreso de datos
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        // envio de trama
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {
                // sino es posible enviar datos se cierra la conexion
                Toast.makeText(MainActivity.this, "La conexion fallo para envio de datos", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
