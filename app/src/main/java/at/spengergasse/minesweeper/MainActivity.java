package at.spengergasse.minesweeper;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    public Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startGame(View view) {
        // get vars from settings
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int cols = sharedPreferences.getInt("default_columns", 6);
        int rows = sharedPreferences.getInt("default_rows", 6);
        int bombs = sharedPreferences.getInt("default_bombs", 6);
        // override defaults when necessary
        Editable s_cols = this.<EditText>findViewById(R.id.cols).getText();
        if (s_cols != null)
            cols = Integer.parseInt(s_cols.toString());
        Editable s_rows = this.<EditText>findViewById(R.id.rows).getText();
        if (s_rows != null)
            cols = Integer.parseInt(s_rows.toString());
        Editable s_bombs = this.<EditText>findViewById(R.id.bombs).getText();
        if (s_bombs != null)
            cols = Integer.parseInt(s_bombs.toString());
        // init game
        game = new Game(cols, rows, bombs);
        // switch to game activity, otherwise null pointer
        setContentView(R.layout.acitvity_game);
        // add listeners to gridView
        GridView gridView = findViewById(R.id.game_grid);
        gridView.setNumColumns(cols);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int score = 0;
                switch (game.flag(position)) {
                    case WIN:
                        winGame();
                        score = 150;
                        break;
                    case HIT:
                        loseGame();
                        break;
                    case VALID:
                        renderBoard();
                        score = 20;
                        break;
                    case INVALID:
                        Toast.makeText(MainActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.whattodo, Toast.LENGTH_SHORT).show();
                }
                increaseScore(score);
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // not sure why this has to return a boolean, but im sure it has a purpose
            // maybe to convert a longClick into a click when necessary...
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int score = 0;
                switch (game.uncover(position)) {
                    case WIN:
                        winGame();
                        score = 150;
                        break;
                    case HIT:
                        loseGame();
                        break;
                    case VALID:
                        renderBoard();
                        score = 50;
                        break;
                    case INVALID:
                        Toast.makeText(MainActivity.this, R.string.invalid, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(MainActivity.this, R.string.whattodo, Toast.LENGTH_SHORT).show();
                        return false;
                }
                increaseScore(score);
                return false;
            }
        });
        // render board for the first time
        renderBoard();
    }

    private void renderBoard() {
        // get GridView
        GridView gridView = findViewById(R.id.game_grid);
        // put data into ArrayAdapter
        // could be inlined, but for readability it is separated
        // migrated string selection to game class, can later be translated to paths
        String[] strings = game.getAllStringsOfAllCells();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, strings);
        // set adapter with new data in it
        gridView.setAdapter(adapter);
    }

    private ImageView getImage(String field) {
        ImageView image = new ImageView(this);
        image.setScaleType(ImageView.ScaleType.CENTER);
        switch (field) {
            case "1":
                image.setImageResource(R.drawable.icon1);
                break;
            case "2":
                image.setImageResource(R.drawable.icon2);
                break;
            case "3":
                image.setImageResource(R.drawable.icon3);
                break;
            case "4":
                image.setImageResource(R.drawable.icon4);
                break;
            case "5":
                image.setImageResource(R.drawable.icon5);
                break;
            case "6":
                image.setImageResource(R.drawable.icon6);
                break;
            case "7":
                image.setImageResource(R.drawable.icon7);
                break;
            case "8":
                image.setImageResource(R.drawable.icon8);
                break;
            case " ":
                image.setImageResource(R.drawable.empty);
            case "X": // hidden
                image.setImageResource(R.drawable.hidden);
            case "F": // flagged
                image.setImageResource(R.drawable.flag);
        }
        return new ImageView(this);
    }



    private void increaseScore(int score) {
        // count up the score | heavily inlined
        this.<TextView>findViewById(R.id.score).setText(String.valueOf(Integer.parseInt(this.<TextView>findViewById(R.id.score).getText().toString()) + score));
    }

    private void loseGame() {
        game.uncoverAll(); // to see the mistakes
        renderBoard();
        Toast.makeText(this, R.string.lose, Toast.LENGTH_LONG).show();
    }

    private void winGame() {
        game.uncoverAll(); // to uncover bombs too
        renderBoard();
        Toast.makeText(this, R.string.win, Toast.LENGTH_LONG).show();
    }

    public void easter (View view) {
        Toast.makeText(this, "You have found the easter egg!", Toast.LENGTH_SHORT).show();
    }

    public void restart(View view) {
        game.restart();
        renderBoard();
        // set score to 0
        TextView score = findViewById(R.id.score);
        score.setText("0");
    }
}

