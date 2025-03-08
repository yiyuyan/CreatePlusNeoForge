package org.xiyu.yee.createplus.utils;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.xiyu.yee.createplus.Createplus;
import org.xiyu.yee.createplus.features.CreativePlusFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
HelpManager {
    private static final Map<String, FeatureHelp> featureHelps = new HashMap<>();

    // ���ܰ��������ݽṹ
    private record FeatureHelp(
        List<String> usage,      // ʹ�÷���
        List<String> notes,      // ע�������ѡ��
        List<String> examples    // ʾ������ѡ��
    ) {}

    // ��ʼ�����й��ܵİ�����Ϣ
    static {
        // �����������ܰ���
        featureHelps.put("��������", new FeatureHelp(
            List.of(
                "1. ʹ��ľ���Ҽ�ѡ���һ����",
                "2. ʹ��ľ���Ҽ�ѡ��ڶ�����",
                "3. ���� /exportbuilding <����>",
                "4. �����ᱣ�浽 buildings/<����>.mcfunction"
            ),
            List.of("�����Ľ����ļ������������浵��ʹ��"),
            List.of(
                "/exportbuilding myhouse",
                "/exportbuilding tower1"
            )
        ));

        // �����칦�ܰ���
        featureHelps.put("������", new FeatureHelp(
            List.of(
                "1. ʹ��ľ���Ҽ�ѡ�����",
                "2. Shift+�Ҽ��л�������(X/Y/Z)",
                "3. ���÷���ʱ���Զ��ڶԳ�λ�÷���",
                "4. �ٴ��Ҽ����������ƶ�λ��"
            ),
            List.of("����������ɫ����Ч����ʾ"),
            List.of("�ʺϽ���ԳƵĽ�������Ǳ�������")
        ));

        // �����ɫ���ܰ���
        featureHelps.put("�����ɫ", new FeatureHelp(
            List.of(
                "1. �ֳֿɱ�ɫ����(����ë��������)",
                "2. ��ס��CTRL������������",
                "3. �������ͬ������ѭ���л�"
            ),
            List.of(
                "֧�ֵķ�������:",
                "- ��ë��������������",
                "- ��̺�����ߡ���",
                "- ���ġ�ǱӰ�е�"
            ),
            null
        ));

        // ���ٽ��칦�ܰ���
        featureHelps.put("���ٽ���", new FeatureHelp(
            List.of(
                "1. �������ܺ��Զ���Ч",
                "2. �Ƴ�������õ���ȴʱ��",
                "3. ���Կ����������÷���"
            ),
            List.of("���ڴ���ģʽ����Ч"),
            null
        ));

        // �����Ϲ��ܰ���
        featureHelps.put("������", new FeatureHelp(
            List.of(
                "1. �� F6 ����/�ر�������",
                "2. ʹ�� WASD �Ϳո������",
                "3. ʹ�������ֵ��ڷ����ٶ�",
                "4. �ٴΰ� F6 ������ʱ�Զ��˳�"
            ),
            List.of(
                "- ���Դ�ǽ�۲�",
                "- ����Ӱ�����ʵ��λ��",
                "- �ʺϲ鿴���κͽ���"
            ),
            List.of("�۲���½������ӽṹʱ�ر�����")
        ));

        // ����Գ���ܰ���
        featureHelps.put("����Գ", new FeatureHelp(
            List.of(
                "1. �������ܺ��Զ�װ���������",
                "2. ���ӷ��ú��ƻ�����ľ���",
                "3. �رչ��ܺ��Զ��ָ�ԭװ��"
            ),
            List.of(
                "- ���ڴ���ģʽ����Ч",
                "- ����Ӱ������ģʽ��ƽ����"
            ),
            null
        ));

        // ҹ�ӹ��ܰ���
        featureHelps.put("ҹ��", new FeatureHelp(
            List.of(
                "1. �������ܺ��Զ���Ч",
                "2. �����Ϸ��������",
                "3. �ںڰ���Ҳ�ܿ�����Χ"
            ),
            List.of("����Ҫʹ��ҹ��ҩˮЧ��"),
            null
        ));

        // ���Ź��ܰ���
        featureHelps.put("����", new FeatureHelp(
            List.of(
                "1. ��ס C ����������",
                "2. ʹ�������ֵ������ű���",
                "3. �ɿ� C ���ָ������ӽ�"
            ),
            List.of(
                "- Ĭ�����ű���Ϊ 4 ��",
                "- ���� 1-10 ��֮�����"
            ),
            null
        ));

        // �ٶȵ��ڹ��ܰ���
        featureHelps.put("�ٶȵ���", new FeatureHelp(
            List.of(
                "1. �������ܺ�ʹ�ÿ�ݼ�����",
                "2. Alt + �����ֵ��������ٶ�",
                "3. Alt + Shift + �����ֵ��ڷ����ٶ�"
            ),
            List.of(
                "- ���ڴ���ģʽ����Ч",
                "- ���Ծ�ȷ�����ƶ��ٶ�",
                "- �ʺϾ�ϸ����ʱʹ��"
            ),
            List.of(
                "��������Ҫ��ȷ�ƶ�ʱ�����ٶ�",
                "�ڳ������ƶ�ʱ����ٶ�"
            )
        ));

        // ��Χ���칦�ܰ���
        featureHelps.put("��Χ����", new FeatureHelp(
            List.of(
                "1. ���ֳַ���ʱ�Զ�����",
                "2. ��Ŀ��λ���γ����η�������",
                "3. ʹ�������ֵ��ڷ�Χ��С",
                "4. ������÷��飬�Ҽ��������"
            ),
            List.of(
                "- �ʺϿ��������������",
                "- �����ڴ������λ�Բ�νṹ",
                "- ֧���������͵ķ���"
            ),
            List.of(
                "�������ν���",
                "�������ն�",
                "�����������"
            )
        ));

        // �������빦�ܰ���
        featureHelps.put("��������", new FeatureHelp(
            List.of(
                "1. ʹ�� /importbuilding <����> ѡ����",
                "2. ����ʾ��ɫԤ����",
                "3. ʹ�� /confirmimport ȷ�ϵ���",
                "4. ʹ�� /cancelimport ȡ������"
            ),
            List.of(
                "- ��ҪOPȨ��(2��)���ܵ���",
                "- �����ļ�����λ�� buildings �ļ���",
                "- ֧�ֵ��������浵�Ľ���"
            ),
            List.of(
                "/importbuilding myhouse",
                "/confirmimport",
                "/cancelimport"
            )
        ));
    }

    // ��ʾͨ�ð���
    public static void showGeneralHelp(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("��6=== CreatePlus ���ܰ��� ==="), false);
        
        // ������Ϣ
        source.sendSuccess(() -> Component.literal("\n��d������Ϣ:"), false);
        source.sendSuccess(() -> Component.literal("��7����: ��b���(God_xiyu)"), false);
        source.sendSuccess(() -> Component.literal("��7����Ⱥ: ��b691870136"), false);
        source.sendSuccess(() -> Component.literal("��c����ʹ�ñ�ģ����ж����ƻ���Υ�߽����ƺ����������"), false);
        
        // �������
        source.sendSuccess(() -> Component.literal("\n��e�������:"), false);
        showCommandHelp(source);

        source.sendSuccess(() -> Component.literal("\n��eʹ�� ��6/features help <������> ��e�鿴���幦�ܵ���ϸ����"), false);
    }

    // ��ʾ�������
    public static void showCommandHelp(CommandSourceStack source) {
        List<String> commands = List.of(
            "/features - ��ʾ���й���",
            "/features <������> - ����/�رչ���",
            "/features help - ��ʾ�˰���",
            "/features help <������> - ��ʾָ�����ܵ���ϸ����",
            ".give <���> <��Ʒ>[{nbt}] [����] - ���ٸ�����Ʒ",
            "/exportbuilding <����> - ����ѡ������",
            "/importbuilding <����> - ���뽨��",
            "/confirmimport - ȷ�ϵ��뽨��",
            "/cancelimport - ȡ�����뽨��"
        );

        for (String cmd : commands) {
            source.sendSuccess(() -> Component.literal("��7" + cmd), false);
        }
    }

    // ��ʾ�ض����ܵİ���
    public static void showFeatureHelp(CommandSourceStack source, String featureName) {
        var features = Createplus.FEATURE_MANAGER.getFeatures();
        CreativePlusFeature targetFeature = features.stream()
            .filter(f -> f.getName().equals(featureName))
            .findFirst()
            .orElse(null);

        if (targetFeature == null) {
            source.sendFailure(Component.literal("��cδ�ҵ�����: ��f" + featureName));
            return;
        }

        // ��ʾ���ܱ���
        source.sendSuccess(() -> Component.literal("��6=== " + featureName + " ���ܰ��� ==="), false);
        
        // ��ʾ��������
        source.sendSuccess(() -> Component.literal("\n��e��������:"), false);
        source.sendSuccess(() -> Component.literal("��7" + targetFeature.getDescription()), false);

        // ��ȡ����ʾ���ܵ���ϸ����
        FeatureHelp help = featureHelps.get(featureName);
        if (help != null) {
            // ��ʾʹ�÷���
            source.sendSuccess(() -> Component.literal("\n��eʹ�÷���:"), false);
            help.usage.forEach(line -> 
                source.sendSuccess(() -> Component.literal("��7" + line), false));

            // ��ʾע���������У�
            if (help.notes != null && !help.notes.isEmpty()) {
                source.sendSuccess(() -> Component.literal("\n��eע������:"), false);
                help.notes.forEach(line -> 
                    source.sendSuccess(() -> Component.literal("��7" + line), false));
            }

            // ��ʾʾ��������У�
            if (help.examples != null && !help.examples.isEmpty()) {
                source.sendSuccess(() -> Component.literal("\n��eʾ��:"), false);
                help.examples.forEach(line -> 
                    source.sendSuccess(() -> Component.literal("��7" + line), false));
            }
        } else {
            // ����û���ض�������Ϣ�Ĺ��ܣ���ʾĬ�ϰ���
            source.sendSuccess(() -> Component.literal("\n��eʹ�÷���:"), false);
            source.sendSuccess(() -> Component.literal("��7ʹ�� /features " + featureName + " ����/�رմ˹���"), false);
        }

        // ��ʾ��ǰ״̬
        String status = targetFeature.isEnabled() ? "��a[������]" : "��7[�ѽ���]";
        source.sendSuccess(() -> Component.literal("\n��e��ǰ״̬: " + status), false);
    }
} 