### java-kanban <br/>
Трекер задач (аналог Trello) - таск-менеджер для управления сроками и задачами команды разработки.
____
### Возможности проекта
Задачи могут быть трёх типов:
- обычные задачи;
- эпики;
- подзадачи. 

Для них должны выполняться следующие условия:
- для каждой подзадачи известно, в рамках какого эпика она выполняется;
- каждый эпик знает, какие подзадачи в него входят;
- завершение всех подзадач эпика считается завершением эпика.

**Проектом представлены:**
- две реализации класса менеджера: один хранит информацию в оперативной памяти, другой — в файле (менеджер запускается на старте программы и управляет всеми задачами);
- функциональность - история просмотров задач;
- функциональность - расставка задач по приоритету и проверка их пересечения по времени выполнения;
- покрытие кода юнит-тестами;
- реализация API, где эндпоинты соответствуют вызовам базовых методов интерфейса TaskManager;
- HTTP-клиент - с его помощью состояние менеджера хранится на отдельном сервере.  
____
### Освоенные технологии и навыки
- изучение основных принципов ООП;
- работа со списками и хеш-таблицами; 
- обработка исключений;
- работа с файлами; 
- написание юнит-тестов;
- создание API; 
- сетевые запросы.
