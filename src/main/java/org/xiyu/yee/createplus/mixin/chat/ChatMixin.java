package org.xiyu.yee.createplus.mixin.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ChatMixin {
    @Inject(method = "sendChat", at = @At("HEAD"), cancellable = true)
    private void onSendChat(String message, CallbackInfo ci) {
        if (message.startsWith(".give ")) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // ����Ƿ��ڴ���ģʽ
            if (!mc.player.isCreative()) {
                mc.player.displayClientMessage(Component.literal("��c������ڴ���ģʽ�²���ʹ�ô�����"), false);
                ci.cancel();
                return;
            }

            // �����������
            String fullCommand = message.substring(6).trim();
            String[] args = parseCommand(fullCommand);
            if (args == null || args.length < 2) {
                mc.player.displayClientMessage(Component.literal("��c�÷�: .give <���> <��Ʒ>[{nbt}] [����]"), false);
                ci.cancel();
                return;
            }

            try {
                // ��ȡ��ҡ���Ʒ������
                String targetPlayer = args[0];
                String itemWithNbt = args[1];
                int count = args.length > 2 ? Integer.parseInt(args[2]) : 1;

                // ����Ƿ���ԭ��Ȩ��
                boolean hasPermission = mc.player.hasPermissions(2);

                if (hasPermission) {
                    // ��Ȩ�ޣ�ʹ��ԭ��give����
                    String vanillaCommand = String.format("give %s %s %d", targetPlayer, itemWithNbt, count);
                    mc.player.connection.sendCommand(vanillaCommand);
                } else {
                    // ��Ȩ�ޣ�ֻ������Լ���Ʒ
                    if (!targetPlayer.equals(mc.player.getName().getString()) && !targetPlayer.equals("@s")) {
                        mc.player.displayClientMessage(Component.literal("��c��Ȩ�޸������������Ʒ"), false);
                        ci.cancel();
                        return;
                    }

                    // ʹ��NBT��ʽ������Ʒ
                    ItemStack stack = createItemWithNBT(itemWithNbt, count);
                    if (stack != null) {
                        boolean success = mc.player.getInventory().add(stack);
                        if (success) {
                            mc.player.displayClientMessage(Component.literal(
                                String.format("��a�Ѹ��� %s %d �� %s", 
                                    mc.player.getName().getString(),
                                    count, 
                                    stack.getDisplayName().getString())
                            ), false);
                        } else {
                            mc.player.displayClientMessage(Component.literal("��c��Ʒ������"), false);
                        }
                    } else {
                        mc.player.displayClientMessage(Component.literal("��c��Ч����ƷID��NBT"), false);
                    }
                }
            } catch (NumberFormatException e) {
                mc.player.displayClientMessage(Component.literal("��c��Ч������"), false);
            } catch (Exception e) {
                mc.player.displayClientMessage(Component.literal("��c����ִ��ʧ��: " + e.getMessage()), false);
            }

            // ȡ��ԭʼ��Ϣ����
            ci.cancel();
        }
    }

    private String[] parseCommand(String command) {
        try {
            String[] result = new String[3];
            int curIndex = 0;
            StringBuilder current = new StringBuilder();
            boolean inNbt = false;
            int nbtDepth = 0;

            for (char c : command.toCharArray()) {
                if (curIndex >= 3) break;

                if (c == '{' && !inNbt) {
                    inNbt = true;
                    nbtDepth++;
                    current.append(c);
                } else if (inNbt) {
                    current.append(c);
                    if (c == '{') nbtDepth++;
                    if (c == '}') {
                        nbtDepth--;
                        if (nbtDepth == 0) inNbt = false;
                    }
                } else if (c == ' ' && !inNbt) {
                    if (current.length() > 0) {
                        result[curIndex++] = current.toString();
                        current = new StringBuilder();
                    }
                } else {
                    current.append(c);
                }
            }

            if (current.length() > 0 && curIndex < 3) {
                result[curIndex] = current.toString();
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private ItemStack createItemWithNBT(String itemWithNbt, int count) {
        try {
            // ������ƷID��NBT
            String itemId;
            CompoundTag nbt = null;

            int nbtStart = itemWithNbt.indexOf('{');
            if (nbtStart != -1) {
                itemId = itemWithNbt.substring(0, nbtStart);
                String nbtString = itemWithNbt.substring(nbtStart);
                nbt = TagParser.parseTag(nbtString);
            } else {
                itemId = itemWithNbt;
            }

            // ������Ʒ
            var item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
            if (item == null) return null;

            ItemStack stack = new ItemStack(item, count);
            if (nbt != null) {
                //stack.setTag(nbt);
            }

            return stack;
        } catch (CommandSyntaxException | IllegalArgumentException e) {
            return null;
        }
    }
} 