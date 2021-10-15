package sv.edu.ues.fia.eisi.controlremoto;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class DispositivosVinculados extends AppCompatActivity {

    // depuracion LOGAT
    private static final String TAG = "Dispositivos conectados";

    // declaración de listView
    ListView idLista;

    // string que se enviara a la actividad principal
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // declaración de campos
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos_vinculados);
    }

    @Override
    public void onResume() {
        super.onResume();
        // -----------------------------------------------------------------------
        verificarEstadoBT();
        // inicializa el array que  contendra lista de dispositivos conectados
        mPairedDevicesArrayAdapter = new ArrayAdapter(this, R.layout.dispositivos_encontrados);
        // presenta los dispositivos encontrados
        idLista = (ListView) findViewById(R.id.idLista);
        idLista.setAdapter(mPairedDevicesArrayAdapter);
        idLista.setOnItemClickListener(mDeviceClickListener);

        // obtiene el adaptador local de bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> paireDevice = mBtAdapter.getBondedDevices();

        // adiciona un dispositivo emparejado al array
        if (paireDevice.size() > 0) {
            for(BluetoothDevice device: paireDevice) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }


    // configurar un onclick para la vista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // obtener la direccion  MAC del dispositivo, que son los ultimos 17 caracteres
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() -17);

            finishAffinity();

            // realiza un intent para iniciar la siguiente actividad
            // mientras como EXTRA_DEVICE_ADDRESS que es la dirección MAC
            Intent intent = new Intent(DispositivosVinculados.this, MainActivity.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intent);
        }
    };

    public void verificarEstadoBT() {
        // comprueba que el dispostivo tenga bluetooth y que esta encendido
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(this, "El dispoitivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "Bluetooth activado");
            } else {
                // solicita al usuario que active bluetooth
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,1);
            }
        }
    }

}
