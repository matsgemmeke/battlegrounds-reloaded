package com.github.matsgemmeke.battlegounds.command;

import com.github.matsgemmeke.battlegrounds.api.game.FreemodeContext;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.command.GiveWeaponCommand;
import com.github.matsgemmeke.battlegrounds.api.entity.BattlePlayer;
import com.github.matsgemmeke.battlegrounds.item.WeaponProvider;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import com.github.matsgemmeke.battlegrounds.locale.Translator;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class GiveWeaponCommandTest {

    private BattlePlayer battlePlayer;
    private FreemodeContext freemodeContext;
    private Player player;
    private PlayerInventory inventory;
    private Translator translator;
    private WeaponProvider weaponProvider;

    @Before
    public void setUp() {
        this.battlePlayer = mock(BattlePlayer.class);
        this.freemodeContext = mock(FreemodeContext.class);
        this.player = mock(Player.class);
        this.inventory = mock(PlayerInventory.class);
        this.translator = mock(Translator.class);
        this.weaponProvider = mock(WeaponProvider.class);

        when(freemodeContext.getBattlePlayer(player)).thenReturn(battlePlayer);
        when(player.getInventory()).thenReturn(inventory);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givesPlayerTheGivenWeapon() {
        String weaponId = "TEST";

        Gun gun = mock(Gun.class);
        when(gun.getName()).thenReturn("TEST");

        WeaponFactory<Gun> weaponFactory = (WeaponFactory<Gun>) mock(WeaponFactory.class);
        when(weaponFactory.make(freemodeContext, weaponId)).thenReturn(gun);

        when(weaponProvider.getWeaponFactory(weaponId)).thenAnswer(mock -> weaponFactory);

        GiveWeaponCommand command = new GiveWeaponCommand(freemodeContext, weaponProvider, translator);
        command.execute(player, weaponId);

        verify(inventory).addItem(any());
    }
}
