package com.mohammadag.restoreinputmethodanimations;

import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.lang.reflect.Method;

import android.app.Dialog;
import android.app.Service;
import android.content.res.Resources;
import android.content.res.XResources;
import android.inputmethodservice.InputMethodService;
import android.provider.Settings;
import android.view.Window;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class RestoreInputMethodAnimations implements IXposedHookZygoteInit {
	int mFancyAnimationId;
	int mAnimationId;

	@Override
	public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
		Resources system = XResources.getSystem();	
		mAnimationId = system.getIdentifier("Animation.InputMethod", "style", "android");
		mFancyAnimationId = system.getIdentifier("Animation.InputMethodFancy", "style", "android");
		XposedHelpers.findAndHookMethod(InputMethodService.class, "initViews", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				Service service = (Service) param.thisObject;
				Object softInputWindowInstance = getObjectField(param.thisObject, "mWindow");
				Method method = Dialog.class.getMethod("getWindow");
				Window window = (Window) method.invoke(softInputWindowInstance);
				if (Settings.Global.getInt(service.getContentResolver(), "fancy_ime_animations", 0) != 0) {
					window.setWindowAnimations(mFancyAnimationId);
				} else {
					window.setWindowAnimations(mAnimationId);
				}
			}
		});
	}
}
