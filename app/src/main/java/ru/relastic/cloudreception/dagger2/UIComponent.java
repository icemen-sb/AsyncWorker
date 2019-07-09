package ru.relastic.cloudreception.dagger2;

import dagger.Subcomponent;
import ru.relastic.cloudreception.domain.MainService.MyServiceProvider;
import ru.relastic.cloudreception.presenter.Activity_List_Person;

@Subcomponent(modules = {UIModule.class})
@UIScope
public interface UIComponent {

    void inject(MyServiceProvider myServiceProvider);
    void inject(Activity_List_Person activity_list_person);
}
