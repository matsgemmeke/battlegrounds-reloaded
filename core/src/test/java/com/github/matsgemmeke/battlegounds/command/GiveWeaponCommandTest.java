package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.api.entity.GamePlayer;
import com.github.matsgemmeke.battlegrounds.api.game.Game;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.command.GiveWeaponCommand;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import com.github.matsgemmeke.battlegrounds.locale.TranslationKey;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private Game game;
    private GameContext context;
    private GamePlayer gamePlayer;
    private Player player;
    private PlayerInventory inventory;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @Before
    public void setUp() {
        this.game = mock(Game.class);
        this.context = mock(GameContext.class);
        this.gamePlayer = mock(GamePlayer.class);
        this.player = mock(Player.class);
        this.inventory = mock(PlayerInventory.class);
        this.translator = mock(Translator.class);
        this.weaponProvider = mock(WeaponProvider.class);

        when(game.getGamePlayer(player)).thenReturn(gamePlayer);
        when(player.getInventory()).thenReturn(inventory);
        when(translator.translate(TranslationKey.DESCRIPTION_GIVEWEAPON.getPath())).thenReturn("description");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givesPlayerTheGivenWeapon() {
        String weaponId = "TEST";

        Firearm firearm = mock(Firearm.class);
        when(firearm.getName()).thenReturn("TEST");

        WeaponFactory<Firearm> weaponFactory = (WeaponFactory<Firearm>) mock(WeaponFactory.class);
        when(weaponFactory.make(context, weaponId)).thenReturn(firearm);

        when(weaponProvider.getWeaponFactory(weaponId)).thenAnswer(mock -> weaponFactory);

        GiveWeaponCommand command = new GiveWeaponCommand(game, context, translator, weaponProvider);
        command.execute(player, weaponId);

        verify(inventory).addItem(any());
    }
}
