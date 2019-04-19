package ru.relastic.asyncworker.dagger2;

import dagger.Subcomponent;
import ru.relastic.asyncworker.domain.MainService.MyServiceProvider;
import ru.relastic.asyncworker.presenter.Activity_Item_Person;
import ru.relastic.asyncworker.presenter.Activity_List_Person;

@Subcomponent(modules = {UIModule.class})
@UIScope
public interface UIComponent {

    void inject(MyServiceProvider myServiceProvider);
    void inject(Activity_List_Person activity_list_person);
    void inject(Activity_Item_Person activity_item_person);
}
