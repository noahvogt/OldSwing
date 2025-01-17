package mod.adrenix.oldswing.config;

import com.electronwill.nightconfig.core.Config;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class TransformerHelper {
    public static int swingSpeed(ClientPlayerEntity player) {
        if (FMLLoader.getDist().isDedicatedServer() || !ConfigHandler.mod_enabled)
            return ConfigHandler.NEW_SPEED;

        Item item = player.getHeldItemMainhand().getItem();
        ResourceLocation source = ForgeRegistries.ITEMS.getKey(item);

        for (Config.Entry entry : ConfigHandler.custom_speeds.entrySet()) {
            if (source != null && source.toString().equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        if (item instanceof SwordItem) {
            return ConfigHandler.sword_speed;
        } else if (item instanceof ToolItem) {
            return ConfigHandler.tool_speed;
        }

        return ConfigHandler.swing_speed;
    }

    public static float getCooldownAnimationFloat(ClientPlayerEntity player, float adjustTicks) {
        return ConfigHandler.prevent_cooldown && ConfigHandler.mod_enabled ? 1.0F : player.getCooledAttackStrength(adjustTicks);
    }

    public static boolean shouldCauseReequipAnimation(@Nonnull ItemStack from, @Nonnull ItemStack to, int slot) {
        if (!ConfigHandler.prevent_reequip || !ConfigHandler.mod_enabled) {
            return ForgeHooksClient.shouldCauseReequipAnimation(from, to, slot);
        }

        boolean fromInvalid = from.isEmpty();
        boolean toInvalid = to.isEmpty();

        if (fromInvalid && toInvalid) return false;
        if (fromInvalid || toInvalid) return true;

        return !ItemStack.areItemsEqualIgnoreDurability(from, to);
    }

    @SuppressWarnings("unused") /* This method is used by ASM */
    public static void armSway(MatrixStack matrixStackIn, Quaternion quaternion) {
        if (!ConfigHandler.prevent_sway || !ConfigHandler.mod_enabled) {
            matrixStackIn.rotate(quaternion);
            matrixStackIn.rotate(quaternion);
        }
    }
}
