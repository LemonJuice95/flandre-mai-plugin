package io.lemonjuice.flan_mai_plugin.image.renderer.completion_table.plan;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlanList {
    private static final Map<String, Plan> PLANS = new HashMap<>();

    public static final Plan FC = register("極", new FCPlan());
    public static final Plan SSS = register("将", new SSSPlan());
    public static final Plan AP = register("神", new APPlan());
    public static final Plan FDX = register("舞舞", new FDXPlan());
    public static final Plan NORMAL = register("", new NormalPlan());

    public static Plan getPlan(String value) {
        return Optional.ofNullable(PLANS.get(value)).orElse(NORMAL);
    }

    private static Plan register(String name, Plan plan) {
        PLANS.put(name, plan);
        return plan;
    }
}
