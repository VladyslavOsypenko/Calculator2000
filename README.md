# Calculator2000
Тестовое задание. 
Это консольное приложение, которое позволяет рассчитывать значение арифметического выражения содержащие целые и дробные
числа, а также математические операции +, -, *, / и ().
В случае, если выражение корректное (по версии данной программы*) - оно будет записано в базу данных Calculations, таблицу expressions
Данная таблица содержит в себе 3 колонки: id (autoincrement), expbody (varchar) и expres (real).
При запуске программы пользователю даются на выбор 4 опции:
1 - Ввести выражение и рассчитать его значение.
2 - Показать данные из бд.
3 - Редактировать существующие выражение (по id)
4 - Поиск по критериям (по id, выражению или по результатам в зависимости от введенного значения и выбранного критерия (>,< или =))
База данных была реализована с помощью СУБД PostgreSQL. 
*NOTE
У меня не было опыта отправки тестового проекта с бд и как делать это правильно я до конца не понимаю. Я создал папку resources under src/main и положил туда 
её бэкап.
