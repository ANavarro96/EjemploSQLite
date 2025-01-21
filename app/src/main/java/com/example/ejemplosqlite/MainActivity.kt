package com.example.ejemplosqlite

import android.app.Dialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.ejemplosqlite.db.SQLPatatasHelper

class MainActivity : AppCompatActivity() {
    lateinit var ver: Button
    lateinit var borrar: Button
    lateinit var guardar: Button
    lateinit var modificar: Button
    lateinit var buscar: Button
    lateinit var nombre: EditText
    lateinit var precio: EditText
    lateinit var lista: TextView
    lateinit var sqlhelper: SQLPatatasHelper
    lateinit var db: SQLiteDatabase

    private val guardarPatatas = View.OnClickListener {
        val patatas = nombre.text.toString()
        val precioPatatas = precio.text.toString().toDouble()


        val nuevoRegistro = ContentValues()
        nuevoRegistro.put("nombre", patatas)
        nuevoRegistro.put("precio", precioPatatas)

        //Insertamos el registro en la base de datos
        db.insert("Patatas", null, nuevoRegistro)
    }

    private val verTodasPatatas = View.OnClickListener {
        val c = db.query(
            "Patatas",  // Tabla a la que se accede (FROM)
            null,  // Columnas que se quieren recuperar.
            // Si ponemos null recogemos todas (*), y podemos poner ,max(),min(),count()...
            null,  // Filtros y condiciones en el WHERE
            null,  // Los parámetros que se aplican en el WHERE (?)
            null,  // Las columnas por las que se hace group By
            null,  // La cláusula HAVING
            null
        ) // Las columnas por las que se ordenan
        /* db.rawQuery("SELECT * FROM PATATAS) */

        //Nos aseguramos de que existe al menos un registro
        // for(c.moveToFirst;!c.afterLast();cur.moveToNext())
        // Con moveToFirst nos movemos al primer registro
        // afterLast() nos indica si es el último elemento
        // moveToNext() nos mueve al siguiente elemento
        lista.text = ""
        var codigo: String
        var nombre: String
        var precop: Double
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            codigo = c.getString(0)
            nombre = c.getString(1)
            precop = c.getDouble(c.getColumnIndexOrThrow("precio"))

            lista.text = lista.text.toString() + codigo + " " + nombre + " " + precop + "\n"
            while (c.moveToNext()) {
                codigo = c.getString(0)
                nombre = c.getString(1)
                precop = c.getDouble(c.getColumnIndexOrThrow("precio"))
                lista.text = lista!!.text.toString() + codigo + " " + nombre + " " + precop + "\n"
            }
        }
        c.close()
    }

    private val buscarPatatas = View.OnClickListener {
        val patatas = nombre.text.toString()
        val c = db.query(
            "Patatas",  // Tabla a la que se accede (FROM)
            null,  // Columnas que se quieren recuperar. Si ponemos null recogemos todas (*)
            "nombre=?",  // Filtros y condiciones en el WHERE
            arrayOf(patatas),  // Los parámetros que se aplican en el WHERE (?)
            null,  // Las columnas por las que se hace group By
            null,  // La cláusula HAVING
            "id DESC"
        ) // Las columnas por las que se ordenan

        //Nos aseguramos de que existe al menos un registro
        lista.text = ""

        // for(c.moveToFirst;!c.afterLast();cur.moveToNext())
        // Con moveToFirst nos movemos al primer registro
        // afterLast() nos indica si es el último elemento
        // moveToNext() nos mueve al siguiente elemento
        var codigo: String
        var nombre: String
        var precop: Double
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            codigo = c.getString(0)
            nombre = c.getString(1)
            precop = c.getDouble(c.getColumnIndexOrThrow("precio"))

            lista.text = lista.text.toString() + codigo + " " + nombre + " " + precop + "\n"
            while (c.moveToNext()) {
                codigo = c.getString(0)
                nombre = c.getString(1)
                precop = c.getDouble(c.getColumnIndexOrThrow("precio"))
                lista.text = lista.text.toString() + codigo + " " + nombre + " " + precop + "\n"
            }
        }
        c.close()
    }

    private val modificarPatatas = View.OnClickListener {
        val patatas = nombre.text.toString()
        val precioPatatas = precio.text.toString().toDouble()

        val nuevoRegistro = ContentValues()
        nuevoRegistro.put("nombre", patatas)
        nuevoRegistro.put("precio", precioPatatas)

        //Actualizamos el registro en la base de datos, modifico el segundo siempre
        db.update("Patatas", nuevoRegistro, "id=2", null)
        // Si en el where pusieramos null, se modificarían todos los registros.
    }

    private val borrarPatatas = View.OnClickListener {
        val patatas = nombre.text.toString()
        //Borro el registro (o los registros) que tengan el mismo nombre
        // db.delete("Patatas", "nombre='" + patatas + "'", null);
        db.delete("Patatas", "nombre=?", arrayOf(patatas))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ver = findViewById<Button>(R.id.ver)
        borrar = findViewById<Button>(R.id.borrar)
        guardar = findViewById<Button>(R.id.guardar)
        modificar = findViewById<Button>(R.id.modificar)
        buscar = findViewById<Button>(R.id.buscar)
        nombre = findViewById<EditText>(R.id.nombre)
        precio = findViewById<EditText>(R.id.precio)
        lista = findViewById<TextView>(R.id.lista)

        guardar.setOnClickListener(guardarPatatas)
        ver.setOnClickListener(verTodasPatatas)
        modificar.setOnClickListener(modificarPatatas)
        borrar.setOnClickListener(borrarPatatas)
        buscar.setOnClickListener(buscarPatatas)


        //Abrimos la base de datos, en modo escritura
        sqlhelper = SQLPatatasHelper(this, "DBPatatas", null, 1)
        println(sqlhelper.databaseName)
        db = sqlhelper.writableDatabase

        val dialogo = DialogoCrear(db)
        dialogo.show(supportFragmentManager, "Dialogo de crear")
    }

    override fun onDestroy() {
        sqlhelper.close()
        super.onDestroy()
    }

    class DialogoCrear(db: SQLiteDatabase) : AppCompatDialogFragment() {


        /* Esta función recoge el evento de pulsar el botón si del diálogo creado aquí abajo */
        var funcionSi: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { dialogInterface, i ->
                usarExecSQL(db)
            }

        fun usarExecSQL(db: SQLiteDatabase) {
            //Inserto datos de ejemplo
            db.execSQL(
                "INSERT INTO Patatas (nombre,precio) " +
                        "VALUES ('" + "Lays" + "', " + 1.5 + ")"
            )

            db.execSQL(
                "INSERT INTO Patatas (nombre,precio) " +
                        "VALUES ('" + "Pringles" + "', " + 2 + ")"
            )

            db.execSQL(
                "INSERT INTO Sabor (nombre) " +
                        "VALUES ('" + "Sal" + "')"
            )

            db.execSQL(
                "INSERT INTO Sabor (nombre) " +
                        "VALUES ('" + "Campesinas" + "')"
            )

            db.execSQL(
                "INSERT INTO PatataSabor (idPatata,idSabor) " +
                        "VALUES (" + "1" + "," + "2" + ")"
            )

            db.execSQL(
                "INSERT INTO PatataSabor (idPatata,idSabor) " +
                        "VALUES (" + "2" + "," + "1" + ")"
            )
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Importante")
                .setMessage("¿Quieres crear datos de prueba? ")
                .setPositiveButton("Sí!", funcionSi)
            builder.setNegativeButton("No!", null)
            return builder.create()
        }
    }





}