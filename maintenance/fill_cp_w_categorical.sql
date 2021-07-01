set sql_safe_updates=0;

update Couples set relationship_type=(select text from CP_Categorical_Choices where qID=12 order by rand() limit 1);

update Couples set num_children = (floor(rand()*(3)));
update Couples set oldest_child=null, youngest_child=null;
update Couples set youngest_child='No Children', oldest_child='No Children' where num_children=0;
update Couples set youngest_child=(select text from CP_Categorical_Choices where qID=6 order by rand() limit 1) where youngest_child is null;
update Couples set oldest_child=youngest_child where oldest_child is null and num_children=1;
update Couples set oldest_child=(select text from CP_Categorical_Choices where qID=5 order by rand() limit 1) where oldest_child is null;

select * from CP_Categorical_Choices where qID=13;
update Couples set children_at_home='Never' where num_children=0;
update Couples set children_at_home=(select text from CP_Categorical_Choices where qID=13 order by rand() limit 1) where num_children>0;

update Partner set gender=(select text from CP_Categorical_Choices where qID=1 order by rand() limit 1);
update Partner set race=(select text from CP_Categorical_Choices where qID=2 order by rand() limit 1);
update Partner set time_in_area=(select text from CP_Categorical_Choices where qID=3 order by rand() limit 1);
update Partner set income_range=(select text from CP_Categorical_Choices where qID=4 order by rand() limit 1);
update Partner set smoke=(select text from CP_Categorical_Choices where qID=8 order by rand() limit 1);
update Partner set drink=(select text from CP_Categorical_Choices where qID=9 order by rand() limit 1);
update Partner set politics=(select text from CP_Categorical_Choices where qID=10 order by rand() limit 1);
update Partner set religion=(select text from CP_Categorical_Choices where qID=11 order by rand() limit 1);