package io.anuke.mindustry.ui.dialogs;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.core.ContentLoader;
import io.anuke.mindustry.game.Content;
import io.anuke.mindustry.game.UnlockableContent;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.ucore.scene.event.HandCursorListener;
import io.anuke.ucore.scene.ui.Image;
import io.anuke.ucore.scene.ui.ScrollPane;
import io.anuke.ucore.scene.ui.Tooltip;
import io.anuke.ucore.scene.ui.layout.Table;
import io.anuke.ucore.scene.utils.UIUtils;

import static io.anuke.mindustry.Vars.control;

public class UnlocksDialog extends FloatingDialog{

    public UnlocksDialog(){
        super("$text.unlocks");

        addCloseButton();
        shown(this::rebuild);
        onResize(this::rebuild);
    }

    void rebuild(){
        content().clear();

        Table table = new Table();
        table.margin(20);
        ScrollPane pane = new ScrollPane(table, "clear-black");

        OrderedMap<String, Array<Content>> allContent = ContentLoader.getContentMap();

        for(String key : allContent.orderedKeys()){
            Array<Content> array = allContent.get(key);
            if(array.size == 0 || !(array.first() instanceof UnlockableContent)) continue;

            table.add("$content." + key + ".name").growX().left().color(Palette.accent);
            table.row();
            table.addImage("white").growX().pad(5).padLeft(0).padRight(0).height(3).color(Palette.accent);
            table.row();
            table.table(list -> {
                list.left();

                int maxWidth = UIUtils.portrait() ? 7 : 13;
                int size = 8 * 6;

                int count = 0;

                for(int i = 0; i < array.size; i++){
                    UnlockableContent unlock = (UnlockableContent) array.get(i);

                    if(unlock.isHidden()) continue;

                    Image image = control.database().isUnlocked(unlock) ? new Image(unlock.getContentIcon()) : new Image("icon-locked");
                    image.addListener(new HandCursorListener());
                    list.add(image).size(size).pad(3);

                    if(control.database().isUnlocked(unlock)){
                        image.clicked(() -> Vars.ui.content.show(unlock));
                        image.addListener(new Tooltip<>(new Table("clear"){{
                            add(unlock.localizedName());
                            margin(4);
                        }}));
                    }

                    if((++count) % maxWidth == 0){
                        list.row();
                    }
                }
            }).growX().left().padBottom(10);
            table.row();
        }

        content().add(pane);
    }
}
