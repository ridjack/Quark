package vazkii.quark.api;

import com.google.common.collect.Lists;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.Logger;
import vazkii.quark.api.flag.ConfigFlagManager;

import java.util.List;

public class Module {

	public String displayName = "";
	public String lowercaseName = "";
	public String description = "";
	public List<String> antiOverlap = null;
	public boolean hasSubscriptions = false;
	public List<Dist> subscriptionTarget = Lists.newArrayList(Dist.CLIENT, Dist.DEDICATED_SERVER);
	public boolean enabledByDefault = true;

	private boolean forcefullyDisabled = false;
	private boolean firstLoad = true;
	public boolean enabled = false;
	public boolean ignoreAntiOverlap = false;

	public void construct() {
		// NO-OP
	}

	public void modulesStarted() {
		// NO-OP
	}
	
	public void buildConfigSpec(ForgeConfigSpec.Builder builder, List<Runnable> callbacks) {
		// NO-OP
	}
	
	public void configChanged() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void configChangedClient() {
		// NO-OP
	}
	
	public void earlySetup() {
		// NO-OP
	}
	
	public void setup() {
		// NO-OP
	}
	
	@OnlyIn(Dist.CLIENT)
	public void clientSetup() {
		// NO-OP
	}

	@OnlyIn(Dist.CLIENT)
	public void modelRegistry() {
		// NO-OP
	}
	
	public void loadComplete() {
		// NO-OP
	}
	
	public void pushFlags(ConfigFlagManager manager) {
		// NO-OP
	}
	
	public final void setEnabled(Logger log, boolean enabled) {
		if(firstLoad)
			log.info("Loading Module " + displayName);
		firstLoad = false;
		
		if(!ignoreAntiOverlap && antiOverlap != null) {
			ModList list = ModList.get();
			for(String s : antiOverlap)
				if(list.isLoaded(s))
					return;
		}

		if (firstLoad) {
			forcefullyDisabled = MinecraftForge.EVENT_BUS.post(new ModuleLoadedEvent(this));
			if (forcefullyDisabled)
				log.info("Module " + displayName + " forcefully disabled");
		}

		if (forcefullyDisabled)
			enabled = false;
		
		setEnabledAndManageSubscriptions(enabled);
	}
	
	private void setEnabledAndManageSubscriptions(boolean enabled) {
		boolean wasEnabled = this.enabled;
		this.enabled = enabled;
		
		if(hasSubscriptions && subscriptionTarget.contains(FMLEnvironment.dist) && wasEnabled != enabled) {
			if(enabled)
				MinecraftForge.EVENT_BUS.register(this);
			else MinecraftForge.EVENT_BUS.unregister(this);
		}
	}
	
}