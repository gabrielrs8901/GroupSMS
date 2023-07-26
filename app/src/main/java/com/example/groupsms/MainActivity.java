package com.example.groupsms;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.Permission;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<PojoCliente> listaClientes;
    Button send;
    Button addClientes;
    Button addContactosCompartidos;
    Button depurarContactos;
    TextView cantClientes;
    EditText mensaje;
    ListView lvClientes;
    ArrayAdapter<String> listaAdapter;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listaClientes = new ArrayList<>();
        send = findViewById(R.id.send);
        addClientes = findViewById(R.id.addDestinatario);
        addContactosCompartidos = findViewById(R.id.addDestinatariosCompartidos);
        depurarContactos = findViewById(R.id.depurarContactos);
        cantClientes = findViewById(R.id.cantidadClientes);
        mensaje = findViewById(R.id.mensaje);
        lvClientes = findViewById(R.id.lvClientes);

//        Recojer el texto que envian compartido por otra imagen
        ObtenerTextoCompartido();


        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                for (PojoCliente client : listaClientes) {
                    String text = mensaje.getText().toString().replaceAll("#cliente", client.getNombreCliente());
                    SmsManager sms = SmsManager.getDefault();
//                    sms.sendTextMessage(client.getNumeroCliente(), null, text, null, null);
                    ArrayList<String> parts = sms.divideMessage(text);
                    int numParts = parts.size();
                    ArrayList<PendingIntent> sentIntents = new ArrayList<>(numParts);
                    ArrayList<PendingIntent> deliveryIntents = new ArrayList<>(numParts);
                    for (int i = 0; i < numParts; i++) {
                        sentIntents.add(null);
                        deliveryIntents.add(null);
                    }
                    sms.sendMultipartTextMessage(client.getNumeroCliente(), null, parts, sentIntents, deliveryIntents);

                }
                Toast.makeText(MainActivity.this, "Mensajes enviados", Toast.LENGTH_SHORT).show();


//                String phoneNumber = "3057205468";
//                String message = "Mensaje de texto largo que deseas enviar";
//
//                SmsManager smsManager = SmsManager.getDefault();
//                ArrayList<String> parts = smsManager.divideMessage(message);
//                int numParts = parts.size();
//
//                ArrayList<PendingIntent> sentIntents = new ArrayList<>(numParts);
//                ArrayList<PendingIntent> deliveryIntents = new ArrayList<>(numParts);
//                for (int i = 0; i < numParts; i++) {
//                    sentIntents.add(null);
//                    deliveryIntents.add(null);
//                }
//
//                smsManager.sendMultipartTextMessage(phoneNumber, null, parts, sentIntents, deliveryIntents);


//                for (PojoCliente client : listaClientes) {
//                    String text = mensaje.getText().toString().replaceAll("#cliente", client.getNombreCliente());
//                    SmsManager sms = SmsManager.getDefault();
//                    sms.sendTextMessage(client.getNumeroCliente(), null, text, null, null);
//                }
//                Toast.makeText(MainActivity.this, "Mensajes enviados", Toast.LENGTH_SHORT).show();
            }
        });

        addClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Get the layout inflater
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                View vista = inflater.inflate(R.layout.dialog_add_cliente, null);
                Button btnSalvar = vista.findViewById(R.id.btnSalvar);
                Button btnCancelar = vista.findViewById(R.id.btnCancelar);
                EditText nombreCliente = vista.findViewById(R.id.nombreCliente);
                EditText numeroCliente = vista.findViewById(R.id.numeroCliente);

                builder.setView(vista);
                AlertDialog entrada = builder.create();
                entrada.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                entrada.show();


                btnSalvar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PojoCliente cliente = new PojoCliente(nombreCliente.getText().toString(), "1" + numeroCliente.getText().toString());
                        listaClientes.add(cliente);
                        Toast.makeText(MainActivity.this, "Agregado: " + cliente.getNombreCliente() + " +" + cliente.getNumeroCliente(), Toast.LENGTH_SHORT).show();
                        cantClientes.setText("Cantidad de clientes: " + listaClientes.size());

                        ArrayList<String> nombre_numero_cliente = new ArrayList<>();
                        for (PojoCliente client : listaClientes) {
                            nombre_numero_cliente.add(client.getNombreCliente() + "\n+" + client.getNumeroCliente());
                        }
                        listaAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, nombre_numero_cliente);
                        lvClientes.setAdapter(listaAdapter);
                        nombreCliente.setText("");
                        numeroCliente.setText("");
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        entrada.cancel();
                    }
                });
            }
        });

        depurarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaClientes.clear();
                if (mensaje.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(MainActivity.this, "No hay contactos que depurar", Toast.LENGTH_SHORT).show();
                } else {
                    String textoDepurar = mensaje.getText().toString();
//                  Quito los parentisis de los numeros de telefonos
                    textoDepurar = textoDepurar.replaceAll("\\(", "");
                    textoDepurar = textoDepurar.replaceAll("\\)", "");
                    textoDepurar = textoDepurar.replaceAll("-", "");
//                    Separo por saltos de lineas
                    String[] cadenaSeparada = textoDepurar.split("\n");
//                    Defino las listas donde voy a agrupar los nombres y los numeros
                    ArrayList<String> nombres = new ArrayList<>();
                    ArrayList<String> numerosMoviles = new ArrayList<>();
//                    Me aseguro que los nombres y los numeros coincidan en el lugar de las listas
//                    por ejemplo Maria que esta en la posicion 3 de la lista nombres, tenga su numero de telefono en la
//                    posicion 3 de la lista de numeros
                    boolean esNombre = true;
                    for (int i = 0; i < cadenaSeparada.length; i++) {
                        if (esNombre) {
                            if (Character.isLetter(cadenaSeparada[i].charAt(0))) {
                                nombres.add(cadenaSeparada[i]);
                                esNombre = false;
                            }
                        } else if (!esNombre) {
                            if (Character.isDigit(cadenaSeparada[i].charAt(0))) {
                                numerosMoviles.add(cadenaSeparada[i]);
                                esNombre = true;
                            }
                        }
                    }

//                    Depuro los nombres para eliminar los apellidos o segundos nombres y solo dejarlo con uno
                    for (int i = 0; i < nombres.size(); i++) {
                        String nombreCompleto = nombres.get(i);
                        String primerNombre = nombreCompleto.split(" ")[0];
                        nombres.set(i, primerNombre);
                    }
//                    Depuro los numeros para eliminar los espacios
                    for (int i = 0; i < numerosMoviles.size(); i++) {
                        String numeroDepurado = numerosMoviles.get(i).replace(" ", "");
                        numerosMoviles.set(i, numeroDepurado);
                    }
//                    Creo mi lista de clientes con sus nombres y numeros y lo muestro en el cuadro de mensaje
//                    para que el usuario chequee que la depuracion se hizo correctamente o pueda corregir algun error
                    if (nombres.size() != numerosMoviles.size()) {
                        Toast.makeText(MainActivity.this, "Hay nombres o numeros sin asignar, revise los contactos compartidos", Toast.LENGTH_SHORT).show();
                    } else {
                        String contactosDepurados = "";
                        for (int i = 0; i < nombres.size(); i++) {
                            contactosDepurados += nombres.get(i) + "\n" + numerosMoviles.get(i) + "\n";
                            PojoCliente cl = new PojoCliente(nombres.get(i), "1" + numerosMoviles.get(i));
                            listaClientes.add(cl);
                        }
                        mensaje.setText(contactosDepurados);
                    }
                }
            }
        });
        addContactosCompartidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantClientes.setText("Cantidad de clientes: " + listaClientes.size());

                ArrayList<String> nombre_numero_cliente = new ArrayList<>();
                for (PojoCliente client : listaClientes) {
                    nombre_numero_cliente.add(client.getNombreCliente() + "\n+" + client.getNumeroCliente());
                }
                listaAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, nombre_numero_cliente);
                lvClientes.setAdapter(listaAdapter);
                mensaje.setText("");
                //modificacion git nueva de nueva
            }
        });


    }

    private void ObtenerTextoCompartido() {
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                mensaje.setText(sharedText);
                if (sharedText == null) {
                    Toast.makeText(this, "No se compartió ningún texto", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}