package kr.starly.libs;

import kr.starly.libs.protocol.TinyProtocol;
import kr.starly.libs.scheduler.Do;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class NillLibs extends JavaPlugin {

    @Getter
    private static NillLibs instance;

    @Getter
    private static TinyProtocol tinyProtocol;
//    @Getter
//    private static GlowApi glowApi;

    @Override
    public void onEnable() {
        instance = this;

        Do.init(this);
        tinyProtocol = new TinyProtocol(this);
//        glowApi = new GlowApi(this);
    }

    @Override
    public void onDisable() {
//        glowApi.disable();
    }
}