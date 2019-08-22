package com.bee.yunkong.core;

import java.io.Serializable;

/**
 * Created by yuanshenghong on 2017/3/18.
 * <p>
 * 为了避免重复，事件名称可以随意定义，但是值根据开发者不同而指定不同的区间避免重复
 */

public enum EventTag implements Serializable {
    NoEvent(1),
    sim_changed(2),
    controlled_change_group(101),
    controlled_change_y(102),
    controlled_change_x(103),
    controlled_change_address(104),

    master_change_group(105),
    master_change_y(106),
    master_change_x(107),
    master_change_address(108),
    master_change_add_group(109),
    master_change_add_apks(110),
    master_change_tasks(111),
    master_start_record(112),
    master_switch_to_third(113),
    master_device_list_changed(114),
    master_device_item_changed(115),

    master_queue_task_list_changed(121),
    master_queue_task_item_changed(122),

    client_register_success(116),

    master_register_success(117),

    master_changed(118),

    socket_disconnet(119),

    server_notice(120),

    EventSomething(2);

    private int value;

    private EventTag(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EventTag{" +
                "value=" + value +
                '}';
    }
}
