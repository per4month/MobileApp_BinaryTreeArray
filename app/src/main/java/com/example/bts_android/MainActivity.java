package com.example.bts_android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import model.factory.FactoryType;
import model.structure.BinaryTreeArray;
import model.usertype.prototype.DateTimeType;
import model.usertype.prototype.ProtoType;

public class MainActivity extends AppCompatActivity {

    Switch switchInteger;
    Switch switchDateTime;
    TextView textView;
    EditText deleteText;
    EditText searchText;

    FactoryType factoryType;
    ProtoType protoType;
    BinaryTreeArray btsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        factoryType = new FactoryType();
        protoType = factoryType.getBuilderByName("Integer");
        btsArray = new BinaryTreeArray(protoType.getTypeComparator());

        deleteText = (EditText) findViewById(R.id.delete_text);
        searchText = (EditText) findViewById(R.id.search_text);

        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setHorizontallyScrolling(true);

        switchInteger = (Switch) findViewById(R.id.switch_integer);
        switchDateTime = (Switch) findViewById(R.id.switch_datetime);

        switchInteger.setChecked(true);
        switchDateTime.setChecked(false);

        switchInteger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    switchDateTime.setChecked(false);
                    Toast.makeText(getBaseContext(), "Текущий тип: Integer", Toast.LENGTH_SHORT).show();

                    protoType = factoryType.getBuilderByName("Integer");
                    btsArray = new BinaryTreeArray(protoType.getTypeComparator());
                    textView.setText("");
                }
                else {
                    switchDateTime.setChecked(true);
                    Toast.makeText(getBaseContext(), "Текущий тип: DateTime", Toast.LENGTH_SHORT).show();

                    protoType = factoryType.getBuilderByName("DateTime");
                    btsArray = new BinaryTreeArray(protoType.getTypeComparator());
                    textView.setText("");
                }
            }
        });

        switchDateTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked == true) {
                    switchInteger.setChecked(false);
                    Toast.makeText(getBaseContext(), "Текущий тип: DateTime", Toast.LENGTH_SHORT).show();

                    protoType = factoryType.getBuilderByName("DateTime");
                    btsArray = new BinaryTreeArray(protoType.getTypeComparator());
                    textView.setText("");
                }
                else {
                    switchInteger.setChecked(true);
                    Toast.makeText(getBaseContext(), "Текущий тип: Integer", Toast.LENGTH_SHORT).show();

                    protoType = factoryType.getBuilderByName("Integer");
                    btsArray = new BinaryTreeArray(protoType.getTypeComparator());
                    textView.setText("");
                }
            }
        });
    }

    private void rewrite() {
        textView.setText(btsArray.toString());
    }

    private void generateAlarmDialog(String title, String message, String additionMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getBaseContext(), additionMessage, Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();
    }

    public void addElem(View view) {
        btsArray.addValue(protoType.create());
        rewrite();
    }

    public void balanceTree(View view) {
        btsArray = btsArray.balance();
        rewrite();
        Toast.makeText(getBaseContext(), "Дерево сбалансировано", Toast.LENGTH_SHORT).show();
    }

    public void searchElem(View view) {
        String value = searchText.getText().toString();
        if (!value.equals(""))
        {
            if (btsArray.isIndexAtTree(Integer.parseInt(value))) {
                Object obj = btsArray.getDataAtIndex(Integer.parseInt(value));
                generateAlarmDialog("Результат поиска",
                        "Индекс = " + Integer.parseInt(value) + "\n" +
                                "Значение по индексу = " + obj.toString(),
                        "Повторите ввод!");
            }
            else
                generateAlarmDialog("Ошибка!",
                        "Индекс " + Integer.parseInt(value) +
                                " не соответствует данному дереву!",
                        "Повторите ввод!");
        }
        else
            generateAlarmDialog("Ошибка!",
                    "Введите число в поле!",
                    "Повторите ввод!");
    }

    public void loadBinaryTreeArray(View view) throws IOException {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader((openFileInput("saved.txt"))));
        }
        catch (Exception ex) {
            Toast.makeText(getBaseContext(), "Файл не существует!", Toast.LENGTH_LONG).show();
            return;
        }
        String line;
        line = br.readLine();
        if (line == null) {
            Toast.makeText(getBaseContext(), "Файл пуст!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!protoType.typeName().equals(line)) {
            generateAlarmDialog("Ошибка!",
                    "Измените тип данных, чтобы загрузить файл!",
                    "Измените тип!");
            return;
        }
        btsArray = new BinaryTreeArray(protoType.getTypeComparator());

        while ((line = br.readLine()) != null) {
            try {
                btsArray.addValue(protoType.parseValue(line));
            }
            catch (Exception ex) {
                generateAlarmDialog("Ошибка при чтении файла", "В нем хранится другая структура данных!", "Повторите действие");
                br.close();
                return;
            }
        }
        br.close();

        Toast.makeText(getBaseContext(), "Файл загружен", Toast.LENGTH_LONG).show();
        rewrite();
    }

    public void saveBinaryTreeArray(View view) throws IOException {
        BufferedWriter br = new BufferedWriter(new OutputStreamWriter((openFileOutput("saved.txt", MODE_PRIVATE))));
        br.write(protoType.typeName() + "\n");
        btsArray.forEach(el -> {
            try {
                br.write(el.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        br.close();

        Toast.makeText(getBaseContext(), "Файл сохранён", Toast.LENGTH_LONG).show();
    }

    public void deleteElem(View view) {
        String value = deleteText.getText().toString();
        if (!value.equals(""))
        {
            if (btsArray.isIndexAtTree(Integer.parseInt(value))) {
                btsArray.removeNodeByIndex(Integer.parseInt(value));
                rewrite();
                Toast.makeText(getBaseContext(), "Элемент удалён", Toast.LENGTH_SHORT).show();
            }
            else
                generateAlarmDialog("Ошибка!",
                        "Индекс " + Integer.parseInt(value) +
                                " не соответствует данному дереву!",
                        "Повторите ввод!");
        }
        else
            generateAlarmDialog("Ошибка!",
                    "Введите число в поле!",
                    "Повторите ввод!");
    }
}